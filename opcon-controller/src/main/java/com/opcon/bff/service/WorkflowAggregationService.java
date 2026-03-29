package com.opcon.bff.service;

import com.opcon.bff.client.CwmClient;
import com.opcon.bff.dto.ActionResponse;
import com.opcon.bff.dto.PageResponse;
import com.opcon.bff.dto.WorkflowResponse;
import com.opcon.bff.dto.downstream.CwmJobDto;
import com.opcon.bff.exception.DownstreamServiceException;
import com.opcon.bff.exception.ResourceNotFoundException;
import com.opcon.bff.mapper.WorkflowMapper;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowAggregationService {

    private final CwmClient cwmClient;
    private final WorkflowMapper workflowMapper;

    public Mono<WorkflowResponse> getWorkflow(UUID workflowId) {
        Mono<com.opcon.bff.dto.downstream.CwmWorkflowDto> workflowMono = cwmClient.getWorkflow(workflowId);
        Mono<List<CwmJobDto>> jobsMono = cwmClient.getWorkflowJobs(workflowId)
                .map(jobs -> List.copyOf(jobs))
                .onErrorResume(error -> {
                    log.warn("Returning workflow {} with empty jobs due to downstream failure", workflowId, error);
                    return Mono.just(List.of());
                });

        return Mono.zip(workflowMono, jobsMono)
                .map(tuple -> workflowMapper.toWorkflowResponse(tuple.getT1(), tuple.getT2(), tuple.getT2().isEmpty()))
                .onErrorResume(ResourceNotFoundException.class, Mono::error)
                .onErrorResume(DownstreamServiceException.class, error -> {
                    log.warn("Returning fallback workflow response for {}", workflowId, error);
                    return Mono.just(workflowMapper.fallbackWorkflow(workflowId));
                });
    }

    public Mono<PageResponse<WorkflowResponse>> getWorkflows(int page, int size) {
        return cwmClient.getWorkflows(page, size)
                .map(response -> workflowMapper.toWorkflowPage(response, false))
                .onErrorResume(DownstreamServiceException.class, error -> {
                    log.warn("Returning fallback workflow page page={} size={}", page, size, error);
                    return Mono.just(workflowMapper.fallbackWorkflowPage(page, size));
                });
    }

    public Mono<ActionResponse> retryWorkflow(UUID workflowId) {
        return cwmClient.getWorkflowJobs(workflowId)
                .flatMap(this::resolveRetryableJob)
                .flatMap(job -> cwmClient.retryJob(job.jobId())
                        .thenReturn(ActionResponse.builder()
                                .workflowId(workflowId.toString())
                                .action("RETRY")
                                .status("Accepted")
                                .message("Retry requested for job " + job.jobId())
                                .build()))
                .onErrorResume(ResourceNotFoundException.class, Mono::error)
                .onErrorResume(IllegalStateException.class, error -> Mono.just(ActionResponse.builder()
                        .workflowId(workflowId.toString())
                        .action("RETRY")
                        .status("Skipped")
                        .message(error.getMessage())
                        .build()));
    }

    private Mono<CwmJobDto> resolveRetryableJob(List<CwmJobDto> jobs) {
        return jobs.stream()
                .filter(job -> "FAILED".equalsIgnoreCase(job.status()))
                .filter(job -> job.retryCount() == null || job.maxRetries() == null || job.retryCount() < job.maxRetries())
                .min(Comparator.comparing(CwmJobDto::updatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new IllegalStateException("No retryable failed job found for workflow")));
    }
}

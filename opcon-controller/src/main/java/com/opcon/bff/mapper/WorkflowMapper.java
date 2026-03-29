package com.opcon.bff.mapper;

import com.opcon.bff.dto.JobResponse;
import com.opcon.bff.dto.PageResponse;
import com.opcon.bff.dto.WorkflowResponse;
import com.opcon.bff.dto.downstream.CwmJobDto;
import com.opcon.bff.dto.downstream.CwmWorkflowDto;
import com.opcon.bff.dto.downstream.DownstreamPageResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class WorkflowMapper {

    public WorkflowResponse toWorkflowResponse(CwmWorkflowDto workflow, List<CwmJobDto> jobs, boolean degraded) {
        return WorkflowResponse.builder()
                .workflowId(workflow.workflowId())
                .contentId(workflow.contentId())
                .status(toUiStatus(workflow.status()))
                .jobs(jobs == null ? List.of() : jobs.stream().map(this::toJobResponse).toList())
                .degraded(degraded)
                .build();
    }

    public WorkflowResponse fallbackWorkflow(UUID workflowId) {
        return WorkflowResponse.builder()
                .workflowId(workflowId)
                .contentId(null)
                .status("Unavailable")
                .jobs(List.of())
                .degraded(true)
                .build();
    }

    public PageResponse<WorkflowResponse> toWorkflowPage(DownstreamPageResponse<CwmWorkflowDto> page, boolean degraded) {
        List<WorkflowResponse> content = page.content() == null
                ? List.of()
                : page.content().stream()
                .map(workflow -> toWorkflowResponse(workflow, List.of(), degraded))
                .toList();

        return PageResponse.<WorkflowResponse>builder()
                .content(content)
                .page(page.number())
                .size(page.size())
                .totalElements(page.totalElements())
                .totalPages(page.totalPages())
                .first(page.first())
                .last(page.last())
                .degraded(degraded)
                .build();
    }

    public PageResponse<WorkflowResponse> fallbackWorkflowPage(int page, int size) {
        return PageResponse.<WorkflowResponse>builder()
                .content(List.of())
                .page(page)
                .size(size)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .degraded(true)
                .build();
    }

    public JobResponse toJobResponse(CwmJobDto job) {
        return JobResponse.builder()
                .type(job.jobType())
                .status(toUiStatus(job.status()))
                .build();
    }

    public String toUiStatus(String status) {
        if (status == null || status.isBlank()) {
            return "Unknown";
        }

        return switch (status.toUpperCase(Locale.ROOT)) {
            case "SUCCESS" -> "Completed";
            case "FAILED" -> "Failed";
            case "RUNNING" -> "In Progress";
            default -> toTitleCase(status);
        };
    }

    private String toTitleCase(String value) {
        String lower = value.replace('_', ' ').toLowerCase(Locale.ROOT);
        return String.join(" ", Arrays.stream(lower.split(" "))
                .filter(part -> !part.isBlank())
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .toList());
    }
}

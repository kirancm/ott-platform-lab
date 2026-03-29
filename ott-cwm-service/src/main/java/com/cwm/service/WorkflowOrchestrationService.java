package com.cwm.service;

import com.cwm.config.CwmProperties;
import com.cwm.event.ContentIngestedEvent;
import com.cwm.model.JobEntity;
import com.cwm.model.JobStatus;
import com.cwm.model.JobType;
import com.cwm.model.WorkflowEntity;
import com.cwm.model.WorkflowStatus;
import com.cwm.model.WorkflowType;
import com.cwm.repository.JobRepository;
import com.cwm.repository.WorkflowRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowOrchestrationService {

    private final WorkflowRepository workflowRepository;
    private final JobRepository jobRepository;
    private final WorkflowSchedulerService workflowSchedulerService;
    private final CwmProperties properties;

    @Transactional
    public UUID createWorkflowIfAbsent(ContentIngestedEvent event) {
        return workflowRepository.findByContentId(event.contentId())
                .map(existing -> {
                    log.info("Ignoring duplicate ingestion event for contentId={} workflowId={}",
                            event.contentId(), existing.getWorkflowId());
                    return existing.getWorkflowId();
                })
                .orElseGet(() -> createWorkflow(event.contentId()));
    }

    private UUID createWorkflow(String contentId) {
        WorkflowEntity workflow = WorkflowEntity.builder()
                .contentId(contentId)
                .status(WorkflowStatus.CREATED)
                .workflowType(WorkflowType.VOD)
                .build();

        try {
            WorkflowEntity savedWorkflow = workflowRepository.save(workflow);
            createJobs(savedWorkflow.getWorkflowId());
            log.info("Created workflow {} for contentId={}", savedWorkflow.getWorkflowId(), contentId);
            return savedWorkflow.getWorkflowId();
        } catch (DataIntegrityViolationException exception) {
            WorkflowEntity existing = workflowRepository.findByContentId(contentId)
                    .orElseThrow(() -> exception);
            log.info("Detected duplicate contentId={} during workflow creation, reusing workflowId={}",
                    contentId, existing.getWorkflowId());
            return existing.getWorkflowId();
        }
    }

    private void createJobs(UUID workflowId) {
        JobEntity encode = JobEntity.builder()
                .workflowId(workflowId)
                .jobType(JobType.ENCODE)
                .status(JobStatus.PENDING)
                .retryCount(0)
                .maxRetries(properties.getWorkflow().getDefaultMaxRetries())
                .build();
        JobEntity savedEncode = jobRepository.save(encode);

        JobEntity transcode = JobEntity.builder()
                .workflowId(workflowId)
                .jobType(JobType.TRANSCODE)
                .status(JobStatus.PENDING)
                .retryCount(0)
                .maxRetries(properties.getWorkflow().getDefaultMaxRetries())
                .dependsOnJobId(savedEncode.getJobId())
                .build();
        JobEntity savedTranscode = jobRepository.save(transcode);

        JobEntity pkg = JobEntity.builder()
                .workflowId(workflowId)
                .jobType(JobType.PACKAGE)
                .status(JobStatus.PENDING)
                .retryCount(0)
                .maxRetries(properties.getWorkflow().getDefaultMaxRetries())
                .dependsOnJobId(savedTranscode.getJobId())
                .build();
        jobRepository.save(pkg);
    }

    public void triggerScheduling(UUID workflowId) {
        workflowSchedulerService.scheduleWorkflow(workflowId);
    }

    @Transactional
    public void markWorkflowRunning(UUID workflowId) {
        workflowRepository.findById(workflowId).ifPresent(workflow -> {
            if (workflow.getStatus() == WorkflowStatus.CREATED) {
                workflow.setStatus(WorkflowStatus.RUNNING);
            }
        });
    }

    @Transactional
    public void markWorkflowCompletedIfEligible(UUID workflowId) {
        WorkflowEntity workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new NotFoundException("Workflow not found: " + workflowId));
        List<JobEntity> jobs = jobRepository.findByWorkflowIdOrderByCreatedAtAsc(workflowId);

        boolean allSuccessful = !jobs.isEmpty() && jobs.stream().allMatch(job -> job.getStatus() == JobStatus.SUCCESS);
        if (allSuccessful) {
            workflow.setStatus(WorkflowStatus.COMPLETED);
            log.info("Workflow {} completed", workflowId);
            return;
        }

        boolean anyActive = jobs.stream().anyMatch(job -> job.getStatus() == JobStatus.READY
                || job.getStatus() == JobStatus.RUNNING
                || job.getStatus() == JobStatus.SUCCESS);
        if (workflow.getStatus() != WorkflowStatus.FAILED && anyActive) {
            workflow.setStatus(WorkflowStatus.RUNNING);
        }
    }

    @Transactional
    public void markWorkflowFailed(UUID workflowId) {
        workflowRepository.findById(workflowId).ifPresent(workflow -> {
            workflow.setStatus(WorkflowStatus.FAILED);
            log.warn("Workflow {} failed", workflowId);
        });
    }
}

package com.cwm.service;

import com.cwm.config.CwmProperties;
import com.cwm.event.JobCompletedEvent;
import com.cwm.model.JobEntity;
import com.cwm.model.JobStatus;
import com.cwm.model.WorkflowEntity;
import com.cwm.model.WorkflowStatus;
import com.cwm.repository.JobRepository;
import com.cwm.repository.WorkflowRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobLifecycleService {

    private final JobRepository jobRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowSchedulerService workflowSchedulerService;
    private final CwmProperties properties;

    @Transactional
    public void handleJobCompletion(JobCompletedEvent event) {
        JobEntity job = jobRepository.findById(event.jobId())
                .orElseThrow(() -> new NotFoundException("Job not found: " + event.jobId()));

        if (job.getStatus() == JobStatus.SUCCESS || job.getStatus() == JobStatus.FAILED) {
            log.info("Ignoring duplicate completion for job {} status={}", job.getJobId(), job.getStatus());
            return;
        }

        if (job.getStatus() != JobStatus.RUNNING) {
            log.info("Ignoring completion for job {} because current status is {}", job.getJobId(), job.getStatus());
            return;
        }

        job.setExternalJobId(event.externalJobId());
        if (event.success()) {
            job.setStatus(JobStatus.SUCCESS);
            log.info("Job {} completed successfully", job.getJobId());
            workflowSchedulerService.scheduleWorkflow(job.getWorkflowId());
            markWorkflowCompletedIfEligible(job.getWorkflowId());
            return;
        }

        int updatedRetryCount = job.getRetryCount() + 1;
        job.setRetryCount(updatedRetryCount);

        if (updatedRetryCount <= job.getMaxRetries()) {
            job.setStatus(JobStatus.PENDING);
            long delayMs = calculateBackoffDelay(updatedRetryCount);
            log.warn("Job {} failed on attempt {}. Retrying in {} ms", job.getJobId(), event.attemptNumber(), delayMs);
            workflowSchedulerService.scheduleWorkflowWithDelay(job.getWorkflowId(), delayMs);
            return;
        }

        job.setStatus(JobStatus.FAILED);
        workflowRepository.findById(job.getWorkflowId()).ifPresent(workflow -> workflow.setStatus(WorkflowStatus.FAILED));
        log.error("Job {} exhausted retries and marked workflow {} as failed", job.getJobId(), job.getWorkflowId());
    }

    @Transactional
    public void retryJob(UUID jobId) {
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found: " + jobId));
        WorkflowEntity workflow = workflowRepository.findById(job.getWorkflowId())
                .orElseThrow(() -> new NotFoundException("Workflow not found for job: " + jobId));

        if (job.getStatus() != JobStatus.FAILED) {
            throw new ConflictException("Only failed jobs can be retried manually");
        }

        job.setStatus(JobStatus.PENDING);
        job.setRetryCount(0);
        workflow.setStatus(WorkflowStatus.RUNNING);
        workflowSchedulerService.scheduleWorkflow(job.getWorkflowId());
    }

    private long calculateBackoffDelay(int retryCount) {
        return Math.round(properties.getWorkflow().getInitialBackoffMs()
                * Math.pow(properties.getWorkflow().getBackoffMultiplier(), Math.max(0, retryCount - 1)));
    }

    private void markWorkflowCompletedIfEligible(UUID workflowId) {
        if (jobRepository.countByWorkflowIdAndStatus(workflowId, JobStatus.SUCCESS)
                == jobRepository.findByWorkflowIdOrderByCreatedAtAsc(workflowId).size()) {
            workflowRepository.findById(workflowId).ifPresent(workflow -> workflow.setStatus(WorkflowStatus.COMPLETED));
            log.info("Workflow {} completed", workflowId);
        }
    }
}

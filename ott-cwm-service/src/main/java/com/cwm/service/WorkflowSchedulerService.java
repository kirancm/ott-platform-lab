package com.cwm.service;

import com.cwm.event.JobExecutionEvent;
import com.cwm.model.JobEntity;
import com.cwm.model.JobStatus;
import com.cwm.model.WorkflowEntity;
import com.cwm.model.WorkflowStatus;
import com.cwm.repository.JobRepository;
import com.cwm.repository.WorkflowRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowSchedulerService {

    private final WorkflowRepository workflowRepository;
    private final JobRepository jobRepository;
    private final JobMessagePublisher jobMessagePublisher;
    private final TaskScheduler taskScheduler;

    @Transactional
    public void scheduleWorkflow(UUID workflowId) {
        WorkflowEntity workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new NotFoundException("Workflow not found: " + workflowId));
        if (workflow.getStatus() == WorkflowStatus.FAILED || workflow.getStatus() == WorkflowStatus.COMPLETED) {
            return;
        }

        List<JobEntity> jobs = jobRepository.findByWorkflowIdOrderByCreatedAtAsc(workflowId);
        boolean scheduled = false;
        for (JobEntity job : jobs) {
            if (job.getStatus() != JobStatus.PENDING) {
                continue;
            }
            if (isDependencySatisfied(job)) {
                job.setStatus(JobStatus.READY);
                scheduled = true;
                int attemptNumber = job.getRetryCount() + 1;
                log.info("Scheduling job {} for workflow {} attempt {}", job.getJobId(), workflowId, attemptNumber);
                jobMessagePublisher.publishJob(JobExecutionEvent.builder()
                        .jobId(job.getJobId())
                        .workflowId(job.getWorkflowId())
                        .jobType(job.getJobType())
                        .attemptNumber(attemptNumber)
                        .build());
            }
        }
        if (scheduled && workflow.getStatus() == WorkflowStatus.CREATED) {
            workflow.setStatus(WorkflowStatus.RUNNING);
        }
    }

    public void scheduleWorkflowWithDelay(UUID workflowId, long delayMs) {
        taskScheduler.schedule(() -> scheduleWorkflow(workflowId), Instant.now().plusMillis(delayMs));
    }

    private boolean isDependencySatisfied(JobEntity job) {
        if (job.getDependsOnJobId() == null) {
            return true;
        }
        Optional<JobEntity> dependency = jobRepository.findById(job.getDependsOnJobId());
        return dependency.map(value -> value.getStatus() == JobStatus.SUCCESS).orElse(false);
    }
}

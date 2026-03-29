package com.cwm.service;

import com.cwm.config.CwmProperties;
import com.cwm.event.JobCompletedEvent;
import com.cwm.event.JobExecutionEvent;
import com.cwm.model.JobEntity;
import com.cwm.model.JobStatus;
import com.cwm.repository.JobRepository;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockJobExecutionService {

    private final JobRepository jobRepository;
    private final JobMessagePublisher jobMessagePublisher;
    private final CwmProperties properties;

    @Transactional
    public void execute(JobExecutionEvent event) {
        JobEntity job = jobRepository.findById(event.jobId())
                .orElseThrow(() -> new NotFoundException("Job not found: " + event.jobId()));

        if (job.getStatus() == JobStatus.SUCCESS || job.getStatus() == JobStatus.FAILED || job.getStatus() == JobStatus.RUNNING) {
            log.info("Ignoring duplicate execution delivery for job {} status={}", job.getJobId(), job.getStatus());
            return;
        }

        if (job.getStatus() != JobStatus.READY) {
            log.info("Ignoring job {} because it is not ready. currentStatus={}", job.getJobId(), job.getStatus());
            return;
        }

        job.setStatus(JobStatus.RUNNING);
        job.setExternalJobId("mock-" + UUID.randomUUID());
        log.info("Running job {} attempt {}", job.getJobId(), event.attemptNumber());

        sleepRandomDelay();
        boolean success = ThreadLocalRandom.current().nextDouble() >= properties.getWorkflow().getFailureRate();

        jobMessagePublisher.publishJobCompleted(JobCompletedEvent.builder()
                .jobId(job.getJobId())
                .workflowId(job.getWorkflowId())
                .attemptNumber(event.attemptNumber())
                .success(success)
                .externalJobId(job.getExternalJobId())
                .errorMessage(success ? null : "Mock downstream processing failure")
                .build());
    }

    private void sleepRandomDelay() {
        int min = properties.getWorkflow().getMinExecutionDelayMs();
        int max = properties.getWorkflow().getMaxExecutionDelayMs();
        int delay = ThreadLocalRandom.current().nextInt(min, max + 1);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while simulating job execution", exception);
        }
    }
}

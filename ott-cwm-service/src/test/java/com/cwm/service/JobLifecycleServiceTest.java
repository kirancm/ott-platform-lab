package com.cwm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cwm.config.CwmProperties;
import com.cwm.event.JobCompletedEvent;
import com.cwm.model.JobEntity;
import com.cwm.model.JobStatus;
import com.cwm.model.WorkflowEntity;
import com.cwm.model.WorkflowStatus;
import com.cwm.repository.JobRepository;
import com.cwm.repository.WorkflowRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobLifecycleServiceTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private WorkflowRepository workflowRepository;
    @Mock
    private WorkflowSchedulerService workflowSchedulerService;

    private CwmProperties properties;

    @InjectMocks
    private JobLifecycleService service;

    @BeforeEach
    void setUp() {
        properties = new CwmProperties();
        properties.getWorkflow().setInitialBackoffMs(1000);
        properties.getWorkflow().setBackoffMultiplier(2.0);
        service = new JobLifecycleService(jobRepository, workflowRepository, workflowSchedulerService, properties);
    }

    @Test
    void shouldScheduleRetryWhenJobFailsWithinRetryLimit() {
        UUID workflowId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobEntity job = JobEntity.builder()
                .jobId(jobId)
                .workflowId(workflowId)
                .status(JobStatus.RUNNING)
                .retryCount(0)
                .maxRetries(3)
                .build();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        service.handleJobCompletion(JobCompletedEvent.builder()
                .jobId(jobId)
                .workflowId(workflowId)
                .attemptNumber(1)
                .success(false)
                .externalJobId("mock-1")
                .build());

        assertThat(job.getStatus()).isEqualTo(JobStatus.PENDING);
        assertThat(job.getRetryCount()).isEqualTo(1);
        verify(workflowSchedulerService).scheduleWorkflowWithDelay(workflowId, 1000);
    }

    @Test
    void shouldFailWorkflowWhenRetriesAreExhausted() {
        UUID workflowId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobEntity job = JobEntity.builder()
                .jobId(jobId)
                .workflowId(workflowId)
                .status(JobStatus.RUNNING)
                .retryCount(3)
                .maxRetries(3)
                .build();
        WorkflowEntity workflow = WorkflowEntity.builder().workflowId(workflowId).status(WorkflowStatus.RUNNING).build();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));

        service.handleJobCompletion(JobCompletedEvent.builder()
                .jobId(jobId)
                .workflowId(workflowId)
                .attemptNumber(4)
                .success(false)
                .externalJobId("mock-2")
                .build());

        assertThat(job.getStatus()).isEqualTo(JobStatus.FAILED);
        assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.FAILED);
    }

    @Test
    void shouldCompleteWorkflowWhenFinalJobSucceeds() {
        UUID workflowId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobEntity job = JobEntity.builder()
                .jobId(jobId)
                .workflowId(workflowId)
                .status(JobStatus.RUNNING)
                .retryCount(0)
                .maxRetries(3)
                .build();
        WorkflowEntity workflow = WorkflowEntity.builder().workflowId(workflowId).status(WorkflowStatus.RUNNING).build();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.countByWorkflowIdAndStatus(workflowId, JobStatus.SUCCESS)).thenReturn(3L);
        when(jobRepository.findByWorkflowIdOrderByCreatedAtAsc(workflowId)).thenReturn(List.of(job, job, job));
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));

        service.handleJobCompletion(JobCompletedEvent.builder()
                .jobId(jobId)
                .workflowId(workflowId)
                .attemptNumber(1)
                .success(true)
                .externalJobId("mock-3")
                .build());

        assertThat(job.getStatus()).isEqualTo(JobStatus.SUCCESS);
        assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.COMPLETED);
    }

    @Test
    void shouldRejectManualRetryForNonFailedJob() {
        UUID workflowId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobEntity job = JobEntity.builder().jobId(jobId).workflowId(workflowId).status(JobStatus.RUNNING).build();
        WorkflowEntity workflow = WorkflowEntity.builder().workflowId(workflowId).status(WorkflowStatus.RUNNING).build();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));

        assertThatThrownBy(() -> service.retryJob(jobId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Only failed jobs");
    }
}

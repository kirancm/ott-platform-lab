package com.cwm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cwm.config.CwmProperties;
import com.cwm.event.ContentIngestedEvent;
import com.cwm.model.JobEntity;
import com.cwm.model.WorkflowEntity;
import com.cwm.repository.JobRepository;
import com.cwm.repository.WorkflowRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowOrchestrationServiceTest {

    @Mock
    private WorkflowRepository workflowRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private WorkflowSchedulerService workflowSchedulerService;
    @Captor
    private ArgumentCaptor<JobEntity> jobCaptor;

    private CwmProperties properties;

    @InjectMocks
    private WorkflowOrchestrationService service;

    @BeforeEach
    void setUp() {
        properties = new CwmProperties();
        properties.getWorkflow().setDefaultMaxRetries(3);
        service = new WorkflowOrchestrationService(workflowRepository, jobRepository, workflowSchedulerService, properties);
    }

    @Test
    void shouldReturnExistingWorkflowForDuplicateContentId() {
        UUID workflowId = UUID.randomUUID();
        WorkflowEntity existing = WorkflowEntity.builder().workflowId(workflowId).contentId("movie-1").build();
        when(workflowRepository.findByContentId("movie-1")).thenReturn(Optional.of(existing));

        UUID result = service.createWorkflowIfAbsent(ContentIngestedEvent.builder().contentId("movie-1").build());

        assertThat(result).isEqualTo(workflowId);
        verify(workflowRepository, never()).save(any());
    }

    @Test
    void shouldCreateThreeDependentJobs() {
        UUID workflowId = UUID.randomUUID();
        when(workflowRepository.findByContentId("movie-2")).thenReturn(Optional.empty());
        when(workflowRepository.save(any(WorkflowEntity.class))).thenAnswer(invocation -> {
            WorkflowEntity workflow = invocation.getArgument(0);
            workflow.setWorkflowId(workflowId);
            return workflow;
        });
        when(jobRepository.save(any(JobEntity.class))).thenAnswer(invocation -> {
            JobEntity job = invocation.getArgument(0);
            if (job.getJobId() == null) {
                job.setJobId(UUID.randomUUID());
            }
            return job;
        });

        UUID result = service.createWorkflowIfAbsent(ContentIngestedEvent.builder().contentId("movie-2").build());

        assertThat(result).isEqualTo(workflowId);
        verify(jobRepository, org.mockito.Mockito.times(3)).save(jobCaptor.capture());
        assertThat(jobCaptor.getAllValues()).hasSize(3);
        assertThat(jobCaptor.getAllValues().get(1).getDependsOnJobId()).isEqualTo(jobCaptor.getAllValues().get(0).getJobId());
        assertThat(jobCaptor.getAllValues().get(2).getDependsOnJobId()).isEqualTo(jobCaptor.getAllValues().get(1).getJobId());
    }
}

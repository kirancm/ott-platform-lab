package com.cwm.service;

import com.cwm.dto.JobResponse;
import com.cwm.dto.WorkflowResponse;
import com.cwm.model.JobEntity;
import com.cwm.model.WorkflowEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkflowMapper {

    public WorkflowResponse toWorkflowResponse(WorkflowEntity workflow) {
        return WorkflowResponse.builder()
                .workflowId(workflow.getWorkflowId())
                .contentId(workflow.getContentId())
                .status(workflow.getStatus())
                .workflowType(workflow.getWorkflowType())
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .build();
    }

    public JobResponse toJobResponse(JobEntity job) {
        return JobResponse.builder()
                .jobId(job.getJobId())
                .workflowId(job.getWorkflowId())
                .jobType(job.getJobType())
                .status(job.getStatus())
                .retryCount(job.getRetryCount())
                .maxRetries(job.getMaxRetries())
                .dependsOnJobId(job.getDependsOnJobId())
                .externalJobId(job.getExternalJobId())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}

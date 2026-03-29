package com.cwm.service;

import com.cwm.dto.JobResponse;
import com.cwm.dto.WorkflowResponse;
import com.cwm.repository.JobRepository;
import com.cwm.repository.WorkflowRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkflowQueryService {

    private final WorkflowRepository workflowRepository;
    private final JobRepository jobRepository;
    private final WorkflowMapper workflowMapper;

    public WorkflowResponse getWorkflow(UUID workflowId) {
        return workflowRepository.findById(workflowId)
                .map(workflowMapper::toWorkflowResponse)
                .orElseThrow(() -> new NotFoundException("Workflow not found: " + workflowId));
    }

    public List<JobResponse> getWorkflowJobs(UUID workflowId) {
        if (!workflowRepository.existsById(workflowId)) {
            throw new NotFoundException("Workflow not found: " + workflowId);
        }
        return jobRepository.findByWorkflowIdOrderByCreatedAtAsc(workflowId).stream()
                .map(workflowMapper::toJobResponse)
                .toList();
    }
}

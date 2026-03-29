package com.cwm.controller;

import com.cwm.dto.JobResponse;
import com.cwm.dto.WorkflowResponse;
import com.cwm.service.JobLifecycleService;
import com.cwm.service.WorkflowQueryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowQueryService workflowQueryService;
    private final JobLifecycleService jobLifecycleService;

    @GetMapping("/workflows/{workflowId}")
    public WorkflowResponse getWorkflow(@PathVariable UUID workflowId) {
        return workflowQueryService.getWorkflow(workflowId);
    }

    @GetMapping("/workflows/{workflowId}/jobs")
    public List<JobResponse> getWorkflowJobs(@PathVariable UUID workflowId) {
        return workflowQueryService.getWorkflowJobs(workflowId);
    }

    @PostMapping("/jobs/{jobId}/retry")
    public ResponseEntity<Void> retryJob(@PathVariable UUID jobId) {
        jobLifecycleService.retryJob(jobId);
        return ResponseEntity.accepted().build();
    }
}

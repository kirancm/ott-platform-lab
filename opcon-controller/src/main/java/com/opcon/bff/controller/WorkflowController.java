package com.opcon.bff.controller;

import com.opcon.bff.dto.ActionResponse;
import com.opcon.bff.dto.PageResponse;
import com.opcon.bff.dto.WorkflowResponse;
import com.opcon.bff.service.WorkflowAggregationService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/opcon/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowAggregationService workflowAggregationService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Mono<WorkflowResponse> getWorkflow(@PathVariable UUID id) {
        return workflowAggregationService.getWorkflow(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Mono<PageResponse<WorkflowResponse>> getWorkflows(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return workflowAggregationService.getWorkflows(page, size);
    }

    @PostMapping("/{id}/retry")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Mono<ActionResponse> retryWorkflow(@PathVariable UUID id) {
        return workflowAggregationService.retryWorkflow(id);
    }
}

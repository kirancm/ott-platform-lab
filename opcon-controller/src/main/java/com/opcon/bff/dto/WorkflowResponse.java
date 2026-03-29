package com.opcon.bff.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record WorkflowResponse(
        UUID workflowId,
        String contentId,
        String status,
        List<JobResponse> jobs,
        boolean degraded
) {
}

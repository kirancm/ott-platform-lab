package com.cwm.dto;

import com.cwm.model.WorkflowStatus;
import com.cwm.model.WorkflowType;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record WorkflowResponse(
        UUID workflowId,
        String contentId,
        WorkflowStatus status,
        WorkflowType workflowType,
        Instant createdAt,
        Instant updatedAt
) {
}

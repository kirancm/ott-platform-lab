package com.opcon.bff.dto;

import lombok.Builder;

@Builder
public record ActionResponse(
        String workflowId,
        String action,
        String status,
        String message
) {
}

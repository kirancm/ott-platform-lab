package com.opcon.bff.dto.downstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CwmWorkflowDto(
        UUID workflowId,
        String contentId,
        String status,
        String workflowType,
        Instant createdAt,
        Instant updatedAt
) {
}

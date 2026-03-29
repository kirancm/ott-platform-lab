package com.opcon.bff.dto.downstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CwmJobDto(
        UUID jobId,
        UUID workflowId,
        String jobType,
        String status,
        Integer retryCount,
        Integer maxRetries,
        UUID dependsOnJobId,
        String externalJobId,
        Instant createdAt,
        Instant updatedAt
) {
}

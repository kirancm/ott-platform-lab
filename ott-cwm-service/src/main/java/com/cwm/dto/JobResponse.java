package com.cwm.dto;

import com.cwm.model.JobStatus;
import com.cwm.model.JobType;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record JobResponse(
        UUID jobId,
        UUID workflowId,
        JobType jobType,
        JobStatus status,
        int retryCount,
        int maxRetries,
        UUID dependsOnJobId,
        String externalJobId,
        Instant createdAt,
        Instant updatedAt
) {
}

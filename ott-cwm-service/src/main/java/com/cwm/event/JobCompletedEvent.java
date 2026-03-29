package com.cwm.event;

import java.util.UUID;
import lombok.Builder;

@Builder
public record JobCompletedEvent(
        UUID jobId,
        UUID workflowId,
        boolean success,
        int attemptNumber,
        String externalJobId,
        String errorMessage
) {
}

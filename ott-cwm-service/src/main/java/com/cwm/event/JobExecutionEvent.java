package com.cwm.event;

import com.cwm.model.JobType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record JobExecutionEvent(
        UUID jobId,
        UUID workflowId,
        JobType jobType,
        int attemptNumber
) {
}

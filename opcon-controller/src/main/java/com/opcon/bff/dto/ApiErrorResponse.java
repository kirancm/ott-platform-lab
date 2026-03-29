package com.opcon.bff.dto;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {
}

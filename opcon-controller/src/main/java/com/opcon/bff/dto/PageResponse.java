package com.opcon.bff.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean degraded
) {
}

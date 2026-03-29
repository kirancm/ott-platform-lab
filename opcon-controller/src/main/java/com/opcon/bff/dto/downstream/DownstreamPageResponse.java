package com.opcon.bff.dto.downstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DownstreamPageResponse<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}

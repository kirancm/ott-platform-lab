package com.opcon.bff.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record SearchResponse(
        String query,
        List<SearchResultResponse> results,
        boolean degraded
) {
}

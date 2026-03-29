package com.opcon.bff.dto;

import lombok.Builder;

@Builder
public record SearchResultResponse(
        String id,
        String title,
        String type,
        String description,
        String status
) {
}

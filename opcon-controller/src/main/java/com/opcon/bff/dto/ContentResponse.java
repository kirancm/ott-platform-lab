package com.opcon.bff.dto;

import lombok.Builder;

@Builder
public record ContentResponse(
        String id,
        String title,
        String description,
        Integer releaseYear,
        Integer durationMinutes,
        String language,
        Double rating,
        String genre,
        boolean degraded
) {
}

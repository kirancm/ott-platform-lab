package com.opcon.bff.dto.downstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContentServiceResponse(
        Long id,
        String title,
        String description,
        Integer releaseYear,
        Integer durationMinutes,
        String language,
        Double rating,
        String genre
) {
}

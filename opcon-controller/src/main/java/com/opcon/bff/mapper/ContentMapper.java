package com.opcon.bff.mapper;

import com.opcon.bff.dto.ContentResponse;
import com.opcon.bff.dto.downstream.ContentServiceResponse;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {

    public ContentResponse toResponse(ContentServiceResponse content, boolean degraded) {
        return ContentResponse.builder()
                .id(content.id() == null ? null : String.valueOf(content.id()))
                .title(content.title())
                .description(content.description())
                .releaseYear(content.releaseYear())
                .durationMinutes(content.durationMinutes())
                .language(content.language())
                .rating(content.rating())
                .genre(content.genre())
                .degraded(degraded)
                .build();
    }

    public ContentResponse fallback(String id) {
        return ContentResponse.builder()
                .id(id)
                .title("Unavailable")
                .description(null)
                .releaseYear(null)
                .durationMinutes(null)
                .language(null)
                .rating(null)
                .genre(null)
                .degraded(true)
                .build();
    }
}

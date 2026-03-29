package com.opcon.bff.dto;

import lombok.Builder;

@Builder
public record JobResponse(
        String type,
        String status
) {
}

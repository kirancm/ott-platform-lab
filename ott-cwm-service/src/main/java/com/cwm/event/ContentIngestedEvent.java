package com.cwm.event;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ContentIngestedEvent(@NotBlank String contentId) {
}

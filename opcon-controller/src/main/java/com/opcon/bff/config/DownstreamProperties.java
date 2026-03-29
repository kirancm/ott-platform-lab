package com.opcon.bff.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "downstream")
public record DownstreamProperties(
        @Valid ServiceProperties cwm,
        @Valid ServiceProperties search,
        @Valid ServiceProperties content
) {

    public record ServiceProperties(@NotBlank String baseUrl) {
    }
}

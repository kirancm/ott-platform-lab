package com.opcon.bff.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "opcon.webclient")
public record OpConWebClientProperties(
        @Min(1) int timeoutSeconds,
        @Min(0) int retryAttempts
) {
}

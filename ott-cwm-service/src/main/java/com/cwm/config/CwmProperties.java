package com.cwm.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "cwm")
public class CwmProperties {

    private final Workflow workflow = new Workflow();
    private final Topics topics = new Topics();

    @Getter
    @Setter
    public static class Workflow {
        @Min(0)
        private int defaultMaxRetries = 3;
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        private double failureRate = 0.25;
        @Min(100)
        private int minExecutionDelayMs = 1000;
        @Min(100)
        private int maxExecutionDelayMs = 3000;
        @DecimalMin("1.0")
        private double backoffMultiplier = 2.0;
        @Min(100)
        private long initialBackoffMs = 1000;
    }

    @Getter
    @Setter
    public static class Topics {
        private String contentIngested = "content.ingested";
        private String jobQueue = "job.queue";
        private String jobCompleted = "job.completed";
    }
}

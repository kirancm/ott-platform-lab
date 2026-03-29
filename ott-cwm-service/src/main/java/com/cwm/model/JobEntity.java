package com.cwm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_jobs_workflow_id", columnList = "workflow_id"),
        @Index(name = "idx_jobs_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobEntity {

    @Id
    @Column(name = "job_id", nullable = false, updatable = false)
    private UUID jobId;

    @Column(name = "workflow_id", nullable = false, updatable = false)
    private UUID workflowId;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 32)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private JobStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    @Column(name = "depends_on_job_id")
    private UUID dependsOnJobId;

    @Column(name = "external_job_id", length = 128)
    private String externalJobId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (jobId == null) {
            jobId = UUID.randomUUID();
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}

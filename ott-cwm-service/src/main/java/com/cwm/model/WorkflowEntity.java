package com.cwm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
@Table(name = "workflows")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEntity {

    @Id
    @Column(name = "workflow_id", nullable = false, updatable = false)
    private UUID workflowId;

    @Column(name = "content_id", nullable = false, unique = true, length = 128)
    private String contentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WorkflowStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_type", nullable = false, length = 32)
    private WorkflowType workflowType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (workflowId == null) {
            workflowId = UUID.randomUUID();
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}

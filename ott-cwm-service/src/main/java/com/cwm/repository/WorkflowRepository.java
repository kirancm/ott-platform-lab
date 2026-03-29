package com.cwm.repository;

import com.cwm.model.WorkflowEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<WorkflowEntity, UUID> {

    Optional<WorkflowEntity> findByContentId(String contentId);
}

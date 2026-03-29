package com.cwm.repository;

import com.cwm.model.JobEntity;
import com.cwm.model.JobStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<JobEntity, UUID> {

    List<JobEntity> findByWorkflowIdOrderByCreatedAtAsc(UUID workflowId);

    List<JobEntity> findByWorkflowIdAndStatus(UUID workflowId, JobStatus status);

    Optional<JobEntity> findByWorkflowIdAndJobType(UUID workflowId, com.cwm.model.JobType jobType);

    long countByWorkflowIdAndStatus(UUID workflowId, JobStatus status);

    boolean existsByWorkflowIdAndStatusIn(UUID workflowId, Collection<JobStatus> statuses);
}

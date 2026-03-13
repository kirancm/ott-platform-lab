package com.ott.content.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchHistoryRepository 
       extends JpaRepository<WatchHistoryEntity, Long> {

    List<WatchHistoryEntity> findByUserId(Long userId);

}
package com.ott.content.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ott.content.infrastructure.persistence.WatchHistoryEntity;
import com.ott.content.infrastructure.persistence.WatchHistoryRepository;

@Service
public class WatchHistoryService {

    private final WatchHistoryRepository repository;

    public WatchHistoryService(WatchHistoryRepository repository) {
        this.repository = repository;
    }

    public WatchHistoryEntity save(WatchHistoryEntity entity) {
        return repository.save(entity);
    }

    public List<WatchHistoryEntity> getUserHistory(Long userId) {
        return repository.findByUserId(userId);
    }

}

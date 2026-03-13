package com.ott.content.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ott.content.events.WatchEvent;
import com.ott.content.infrastructure.kafka.WatchEventProducer;
import com.ott.content.infrastructure.persistence.WatchHistoryEntity;
import com.ott.content.infrastructure.persistence.WatchHistoryRepository;

@Service
public class WatchHistoryService {

    private final WatchHistoryRepository repository;
    private final WatchEventProducer watchEventProducer;

    public WatchHistoryService(WatchHistoryRepository repository, WatchEventProducer watchEventProducer) {
        this.repository = repository;
        this.watchEventProducer = watchEventProducer;

    }

    public WatchHistoryEntity save(WatchHistoryEntity entity) {
        WatchEvent event = new WatchEvent(
            entity.getUserId(),
            entity.getMovieId(),
            entity.getWatchProgressSeconds(),
            System.currentTimeMillis()
        );
        watchEventProducer.sendWatchEvent(event);
        System.out.println("WatchEvent sent: " + event);
        return repository.save(entity);
    }

    public List<WatchHistoryEntity> getUserHistory(Long userId) {
        return repository.findByUserId(userId);
    }

}

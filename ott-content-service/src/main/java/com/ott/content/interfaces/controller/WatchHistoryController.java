package com.ott.content.interfaces.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ott.content.application.service.WatchHistoryService;
import com.ott.content.infrastructure.mapper.WatchHistoryMapper;
import com.ott.content.infrastructure.persistence.WatchHistoryEntity;
import com.ott.content.interfaces.dto.WatchHistoryRequest;
import com.ott.content.interfaces.dto.WatchHistoryResponse;

@RestController
@RequestMapping("/watch-history")
public class WatchHistoryController {

    private final WatchHistoryService service;

    public WatchHistoryController(WatchHistoryService service) {
        this.service = service;
    }

    @PostMapping
    public WatchHistoryResponse saveHistory(
            @RequestBody WatchHistoryRequest request) {

        WatchHistoryEntity entity = WatchHistoryMapper.toEntity(request);

        WatchHistoryEntity saved = service.save(entity);

        return WatchHistoryMapper.toResponse(saved);
    }

    @GetMapping("/user/{userId}")
    public List<WatchHistoryResponse> getHistory(@PathVariable Long userId) {

        return service.getUserHistory(userId)
                .stream()
                .map(WatchHistoryMapper::toResponse)
                .toList();
    }

}

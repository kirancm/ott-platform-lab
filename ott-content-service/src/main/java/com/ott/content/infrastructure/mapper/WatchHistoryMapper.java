package com.ott.content.infrastructure.mapper;

import java.time.LocalDateTime;

import com.ott.content.infrastructure.persistence.WatchHistoryEntity;
import com.ott.content.interfaces.dto.WatchHistoryRequest;
import com.ott.content.interfaces.dto.WatchHistoryResponse;

public class WatchHistoryMapper {

    public static WatchHistoryEntity toEntity(WatchHistoryRequest request) {

        WatchHistoryEntity entity = new WatchHistoryEntity();

        entity.setUserId(request.getUserId());
        entity.setMovieId(request.getMovieId());
        entity.setWatchProgressSeconds(request.getWatchProgressSeconds());
        entity.setCompleted(request.getCompleted());
        entity.setLastWatchedAt(LocalDateTime.now());

        return entity;
    }

    public static WatchHistoryResponse toResponse(WatchHistoryEntity entity) {

        WatchHistoryResponse response = new WatchHistoryResponse();

        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setMovieId(entity.getMovieId());
        response.setWatchProgressSeconds(entity.getWatchProgressSeconds());
        response.setCompleted(entity.getCompleted());

        return response;
    }

}

package com.ott.analytics.application.service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final RedisTemplate<String, String> redisTemplate;

    public AnalyticsService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void incrementMovieViews(String movieId) {

        redisTemplate.opsForZSet()
                .incrementScore("trending:movies", movieId, 1);
    }

    public Set<String> getTrendingMovies() {

        return redisTemplate.opsForZSet()
                .reverseRange("trending:movies", 0, 9);
    }
}

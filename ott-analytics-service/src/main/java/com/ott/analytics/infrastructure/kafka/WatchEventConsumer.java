package com.ott.analytics.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ott.analytics.application.service.AnalyticsService;
import com.ott.analytics.events.WatchEvent;

@Component
public class WatchEventConsumer {

    private final AnalyticsService analyticsService;

    public WatchEventConsumer(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @KafkaListener(topics = "watch-events", groupId = "analytics-group")
    public void consume(String message) throws JsonMappingException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        WatchEvent event = mapper.readValue(message, WatchEvent.class);
        analyticsService.incrementMovieViews(event.getContentId().toString());

        System.out.println("Watch event received for movie: " + event.getContentId());
    }
}

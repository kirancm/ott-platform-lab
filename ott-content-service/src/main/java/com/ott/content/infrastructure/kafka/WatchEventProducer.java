package com.ott.content.infrastructure.kafka;

import com.ott.content.events.WatchEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WatchEventProducer {

    private final KafkaTemplate<String, WatchEvent> kafkaTemplate;

    public WatchEventProducer(KafkaTemplate<String, WatchEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendWatchEvent(WatchEvent event) {
        kafkaTemplate.send("watch-events", event);
    }
}
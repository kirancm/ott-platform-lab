package com.cwm.event;

import com.cwm.service.JobLifecycleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCompletedListener {

    private final JobLifecycleService jobLifecycleService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${cwm.topics.job-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onJobCompleted(String payload) {
        JobCompletedEvent event = deserialize(payload, JobCompletedEvent.class);
        log.info("Received job completion event for jobId={} success={}", event.jobId(), event.success());
        jobLifecycleService.handleJobCompletion(event);
    }

    private <T> T deserialize(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize Kafka payload", exception);
        }
    }
}

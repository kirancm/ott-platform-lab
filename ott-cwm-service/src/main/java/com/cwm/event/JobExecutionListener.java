package com.cwm.event;

import com.cwm.service.MockJobExecutionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExecutionListener {

    private final MockJobExecutionService mockJobExecutionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${cwm.topics.job-queue}", groupId = "${spring.kafka.consumer.group-id}")
    public void onJob(String payload) {
        JobExecutionEvent event = deserialize(payload, JobExecutionEvent.class);
        log.info("Received job execution event for jobId={}", event.jobId());
        mockJobExecutionService.execute(event);
    }

    private <T> T deserialize(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize Kafka payload", exception);
        }
    }
}

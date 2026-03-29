package com.cwm.service;

import com.cwm.config.CwmProperties;
import com.cwm.event.JobCompletedEvent;
import com.cwm.event.JobExecutionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobMessagePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CwmProperties properties;
    private final ObjectMapper objectMapper;

    public void publishJob(JobExecutionEvent event) {
        kafkaTemplate.send(properties.getTopics().getJobQueue(), event.jobId().toString(), writeValue(event));
    }

    public void publishJobCompleted(JobCompletedEvent event) {
        kafkaTemplate.send(properties.getTopics().getJobCompleted(), event.jobId().toString(), writeValue(event));
    }

    private String writeValue(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize Kafka payload", exception);
        }
    }
}

package com.cwm.event;

import com.cwm.scheduler.WorkflowTriggerService;
import com.cwm.service.WorkflowOrchestrationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentIngestedListener {

    private final WorkflowOrchestrationService workflowOrchestrationService;
    private final WorkflowTriggerService workflowTriggerService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${cwm.topics.content-ingested}", groupId = "${spring.kafka.consumer.group-id}")
    public void onContentIngested(String payload) {
        ContentIngestedEvent event = deserialize(payload, ContentIngestedEvent.class);
        log.info("Received content ingestion event for contentId={}", event.contentId());
        var workflowId = workflowOrchestrationService.createWorkflowIfAbsent(event);
        workflowTriggerService.trigger(workflowId);
    }

    private <T> T deserialize(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize Kafka payload", exception);
        }
    }
}

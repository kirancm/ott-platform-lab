package com.cwm.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    NewTopic contentIngestedTopic(CwmProperties properties) {
        return TopicBuilder.name(properties.getTopics().getContentIngested())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic jobDispatchTopic(CwmProperties properties) {
        return TopicBuilder.name(properties.getTopics().getJobQueue())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic jobCompletedTopic(CwmProperties properties) {
        return TopicBuilder.name(properties.getTopics().getJobCompleted())
                .partitions(1)
                .replicas(1)
                .build();
    }
}

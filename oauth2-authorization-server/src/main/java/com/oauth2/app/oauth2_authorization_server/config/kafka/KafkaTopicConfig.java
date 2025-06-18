package com.oauth2.app.oauth2_authorization_server.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Slf4j
public class KafkaTopicConfig {
    public static final String NEW_ACCOUNT_CREATED_TOPIC = "newAccountCreated";

    @Bean
    public NewTopic newAccountCreatedTopic() {
        log.info("Definiendo NewTopic {}", NEW_ACCOUNT_CREATED_TOPIC);
        return TopicBuilder.name(NEW_ACCOUNT_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
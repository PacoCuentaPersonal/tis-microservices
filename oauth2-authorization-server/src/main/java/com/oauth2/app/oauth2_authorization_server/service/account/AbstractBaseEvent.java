
package com.oauth2.app.oauth2_authorization_server.service.account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractBaseEvent {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    protected void triggerEventKafka(String topic, Object data) {
        try {
            log.info("Sending event to topic '{}': {}", topic, data.getClass().getSimpleName());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, data);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Success: {}", result.getRecordMetadata().offset());
                } else {
                    log.error("Error: {}", ex.getMessage());
                }
            });

        } catch (Exception e) {
            log.error("Error occurred while sending event to Kafka topic '{}': {}", topic, e.getMessage(), e);
            throw new RuntimeException("Failed to send event to Kafka topic: " + topic, e);
        }
    }
}
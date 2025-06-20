package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.messaging.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component; // Make it a Spring component

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component // Abstract classes that are part of the infrastructure and have dependencies like KafkaTemplate should be components if they are to be instantiated or autowired by Spring, or if their subclasses are.
public abstract class AbstractBaseEvent {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    protected void triggerEventKafka(String topic, Object data) {
        try {
            log.info("Sending event to topic '{}': {}", topic, data.getClass().getSimpleName());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, data);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Success: Sent event to topic '{}', offset: {}", topic, result.getRecordMetadata().offset());
                } else {
                    log.error("Error sending event to topic '{}': {}", topic, ex.getMessage());
                }
            });

        } catch (Exception e) {
            log.error("Error occurred while sending event to Kafka topic '{}': {}", topic, e.getMessage(), e);
            // Consider rethrowing a more specific application exception or a custom infrastructure exception
            throw new RuntimeException("Failed to send event to Kafka topic: " + topic, e);
        }
    }
}

package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.messaging.kafka.publisher;

import com.oauth2.app.oauth2_authorization_server.infrastructure.config.kafka.KafkaTopicConfig; // Path corregido
// AbstractBaseEvent is now in the same package
// import com.oauth2.app.oauth2_authorization_server.service.account.AbstractBaseEvent;
// Corrected import for IAccountEvent (Port Out)
import com.oauth2.app.oauth2_authorization_server.application.port.out.IAccountEvent;
import com.tis.account.NewAccountCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class AccountEventImpl extends AbstractBaseEvent implements IAccountEvent {

    public void sendNewAccountCreatedEvent( NewAccountCreatedEvent event) {
        triggerEventKafka(KafkaTopicConfig.NEW_ACCOUNT_CREATED_TOPIC, event);
    }
}

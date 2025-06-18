package com.oauth2.app.oauth2_authorization_server.service.account;

import com.oauth2.app.oauth2_authorization_server.config.kafka.KafkaTopicConfig;
import com.tis.account.NewAccountCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class AccountEventImpl extends AbstractBaseEvent implements IAccountEvent {

    public void sendNewAccountCreatedEvent( NewAccountCreatedEvent event) {
        triggerEventKafka(KafkaTopicConfig.NEW_ACCOUNT_CREATED_TOPIC, event);
    }
}
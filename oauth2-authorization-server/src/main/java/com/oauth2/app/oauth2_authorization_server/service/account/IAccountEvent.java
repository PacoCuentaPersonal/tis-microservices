package com.oauth2.app.oauth2_authorization_server.service.account;


import com.tis.account.NewAccountCreatedEvent;

public interface IAccountEvent {
    void sendNewAccountCreatedEvent( NewAccountCreatedEvent event);

}

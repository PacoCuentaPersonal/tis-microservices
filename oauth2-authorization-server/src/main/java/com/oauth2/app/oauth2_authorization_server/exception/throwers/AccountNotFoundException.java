package com.oauth2.app.oauth2_authorization_server.exception.throwers;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String message) {
        super(message);
    }
}

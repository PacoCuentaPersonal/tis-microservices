package com.oauth2.app.oauth2_authorization_server.application.exception;

public class AccountAlreadyExistsException extends RuntimeException{
    public AccountAlreadyExistsException(String messsage){
        super(messsage);
    }
}

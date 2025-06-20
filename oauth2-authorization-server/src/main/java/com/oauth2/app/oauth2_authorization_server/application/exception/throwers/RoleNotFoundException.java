package com.oauth2.app.oauth2_authorization_server.application.exception.throwers;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(String message) {
        super(message);
    }
}

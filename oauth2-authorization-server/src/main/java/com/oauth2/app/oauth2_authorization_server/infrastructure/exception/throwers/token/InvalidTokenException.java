package com.oauth2.app.oauth2_authorization_server.infrastructure.exception.throwers.token;

public class InvalidTokenException extends TokenException {

    public InvalidTokenException(String message) {
        super("INVALID_TOKEN", message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super("INVALID_TOKEN", message, cause);
    }
}
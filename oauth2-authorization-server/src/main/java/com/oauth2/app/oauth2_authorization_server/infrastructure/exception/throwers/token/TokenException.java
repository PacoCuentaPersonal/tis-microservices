package com.oauth2.app.oauth2_authorization_server.infrastructure.exception.throwers.token;

public abstract class TokenException extends RuntimeException {
    private final String errorCode;

    protected TokenException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected TokenException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
package com.oauth2.app.oauth2_authorization_server.exception.throwers.token;

public class TokenExpiredException extends TokenException {

    public TokenExpiredException(String message) {
        super("TOKEN_EXPIRED", message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super("TOKEN_EXPIRED", message, cause);
    }
}
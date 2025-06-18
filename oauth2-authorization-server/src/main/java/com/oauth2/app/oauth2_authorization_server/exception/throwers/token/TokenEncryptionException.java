package com.oauth2.app.oauth2_authorization_server.exception.throwers.token;

public class TokenEncryptionException extends TokenException {
    public TokenEncryptionException(String message) {
        super("TOKEN_ENCRYPTION_ERROR", message);
    }
    public TokenEncryptionException(String message, Throwable cause) {
        super("TOKEN_ENCRYPTION_ERROR", message, cause);
    }
}

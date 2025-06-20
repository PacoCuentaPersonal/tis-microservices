package com.oauth2.app.oauth2_authorization_server.infrastructure.exception.throwers.token;

public class TokenDecryptionException extends TokenException {

    public TokenDecryptionException(String message) {
        super("TOKEN_DECRYPTION_ERROR", message);
    }

    public TokenDecryptionException(String message, Throwable cause) {
        super("TOKEN_DECRYPTION_ERROR", message, cause);
    }
}
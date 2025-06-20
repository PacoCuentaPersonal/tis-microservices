package com.oauth2.app.oauth2_authorization_server.infrastructure.exception.throwers.token;

public class TokenKeyNotFoundException extends TokenException {
    private final String missingKey;

    public TokenKeyNotFoundException(String key) {
        super("TOKEN_KEY_NOT_FOUND", "Key not found in token: " + key);
        this.missingKey = key;
    }
    public String getMissingKey() {
        return missingKey;
    }
}
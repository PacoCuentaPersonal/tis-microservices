package com.oauth2.app.oauth2_authorization_server.domain.enums;

public enum ClientType {
    PUBLIC("Public Client (SPA, Mobile, Desktop)"),
    CONFIDENTIAL("Confidential Client (Backend, Server-side)");

    private final String description;

    ClientType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

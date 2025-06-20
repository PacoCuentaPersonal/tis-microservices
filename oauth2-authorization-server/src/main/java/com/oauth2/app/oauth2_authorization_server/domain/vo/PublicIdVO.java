package com.oauth2.app.oauth2_authorization_server.domain.vo;

import java.util.UUID;
import java.util.Objects;

public record PublicIdVO(UUID value) {
    public PublicIdVO {
        Objects.requireNonNull(value, "UUID value cannot be null for PublicIdVO");
    }

    public static PublicIdVO of(String uuidString) {
        try {
            return new PublicIdVO(UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            // Handle invalid UUID string appropriately, e.g., throw a custom exception or return null/Optional.empty()
            // For simplicity here, re-throwing or wrapping.
            throw new IllegalArgumentException("Invalid UUID string: " + uuidString, e);
        }
    }

    public static PublicIdVO generate() {
        return new PublicIdVO(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

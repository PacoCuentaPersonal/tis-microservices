package com.tis.account;

import java.util.UUID;

public record NewAccountCreatedEvent(
        UUID publicID,
        String email,
        String username,
        String url
) {
}

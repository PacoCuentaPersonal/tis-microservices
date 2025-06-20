package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response;

import java.util.UUID;

public record PermissionsResponse(
        UUID publicId,
        String code,
        String name,
        String description
) {
}

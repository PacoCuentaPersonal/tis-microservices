package com.oauth2.app.oauth2_authorization_server.dto.response;

import java.util.UUID;

public record PermissionAssignmentResponse(
        UUID publicId,
        String code,
        String name,
        boolean assigned
) {
}
package com.oauth2.app.oauth2_authorization_server.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoleDetailsResponse(
        UUID publicId,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<PermissionAssignmentResponse> permissions,
        int totalPermissions,
        int assignedPermissions
) {
}
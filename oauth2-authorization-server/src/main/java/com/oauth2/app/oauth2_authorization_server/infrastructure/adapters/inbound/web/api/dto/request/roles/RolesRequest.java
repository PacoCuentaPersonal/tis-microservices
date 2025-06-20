package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record RolesRequest(
        @NotEmpty(message = "Name cannot be empty")
        String name,
        String description,
        @NotNull(message = "Permissions cannot be null")
        List<UUID> publicIdPermissions) {
}

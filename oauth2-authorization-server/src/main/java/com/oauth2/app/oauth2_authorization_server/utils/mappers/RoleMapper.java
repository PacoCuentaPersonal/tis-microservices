package com.oauth2.app.oauth2_authorization_server.utils.mappers;

import com.oauth2.app.oauth2_authorization_server.dto.response.RolesResponse;
import com.oauth2.app.oauth2_authorization_server.models.entity.Roles;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoleMapper {
    public static RolesResponse toResponse(Roles role) {
        if (role == null) {
            return null;
        }
        return new RolesResponse(
                role.getPublicId(),
                role.getName(),
                role.getDescription(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    public static List<RolesResponse> toResponseList(List<Roles> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(RoleMapper::toResponse)
                .collect(Collectors.toList());
    }

}

package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.mapper;

// Import DTO and Domain model
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.RolesResponse; // Path corregido
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component // Los mappers pueden ser componentes de Spring para facilitar su inyección
public class RoleMapper {
    public RolesResponse toResponse(Role role) { // Changed parameter to domain model Role
        if (role == null) {
            return null;
        }
        return new RolesResponse(
                role.getPublicId() != null ? role.getPublicId().value() : null,
                role.getName(),
                role.getDescription(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    public List<RolesResponse> toResponseList(List<Role> roles) { // Changed parameter to List of domain model Role
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(this::toResponse) // Use instance method if this mapper is a Spring bean
                .collect(Collectors.toList());
    }
}

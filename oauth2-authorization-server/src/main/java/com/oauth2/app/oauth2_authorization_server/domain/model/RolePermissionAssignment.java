package com.oauth2.app.oauth2_authorization_server.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionAssignment {

    // The Role and Permission objects themselves, or their IDs (e.g., PublicIdVOs)
    // Using full domain objects here for richness, but IDs could also be used depending on context.
    private Role role;
    private Permission permission;

    private Boolean active;

    // Audit fields for the assignment itself
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}

package com.oauth2.app.oauth2_authorization_server.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles_permissions", schema = "oauth2")
public class RolesPermissions extends BaseAuditEntity {
    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private Permissions permission;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Roles role;

    @Column(name = "active")
    private Boolean active;
}
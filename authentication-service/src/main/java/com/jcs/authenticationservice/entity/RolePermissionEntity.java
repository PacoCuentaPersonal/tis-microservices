package com.jcs.authenticationservice.entity;

import com.jcs.authenticationservice.shared.jpa.BaseEntityWithPublicId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role_permission" ,schema = "app")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionEntity extends BaseEntityWithPublicId<RolePermissionId> {
    @EmbeddedId
    private RolePermissionId id;

    private boolean active;

    @ManyToOne (fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id",nullable = false)
    private RoleEntity role;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id",nullable = false)
    private PermissionEntity permission;
}

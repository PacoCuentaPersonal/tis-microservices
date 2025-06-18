package com.oauth2.app.oauth2_authorization_server.interfaces.repository;

import com.oauth2.app.oauth2_authorization_server.models.entity.Permissions;
import com.oauth2.app.oauth2_authorization_server.models.entity.RolePermissionId;
import com.oauth2.app.oauth2_authorization_server.models.entity.RolesPermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaRolesPermissions extends JpaRepository<RolesPermissions, RolePermissionId> {
    @Query("SELECT rp.permission FROM RolesPermissions rp " +
            "WHERE rp.role.publicId = :publicId " +
            "AND rp.active = true " +
            "AND rp.permission.active = true")
    List<Permissions> findPermissionsByRolePublicId(@Param("publicId") UUID publicId);
}
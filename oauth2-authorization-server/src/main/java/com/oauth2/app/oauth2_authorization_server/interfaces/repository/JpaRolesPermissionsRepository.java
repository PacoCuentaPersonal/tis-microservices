package com.oauth2.app.oauth2_authorization_server.interfaces.repository;

import com.oauth2.app.oauth2_authorization_server.models.entity.RolePermissionId;
import com.oauth2.app.oauth2_authorization_server.models.entity.RolesPermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRolesPermissionsRepository extends JpaRepository<RolesPermissions, RolePermissionId> {

    List<RolesPermissions> findByRoleIdAndActiveTrue(Integer roleId);

    List<RolesPermissions> findByRoleId(Integer roleId);

    Optional<RolesPermissions> findByRoleIdAndPermissionId(Integer roleId, Integer permissionId);

    @Query("""
        SELECT rp FROM RolesPermissions rp 
        WHERE rp.role.id = :roleId 
        AND rp.permission.publicId IN :permissionPublicIds
    """)
    List<RolesPermissions> findByRoleIdAndPermissionPublicIds(
            @Param("roleId") Long roleId,
            @Param("permissionPublicIds") List<UUID> permissionPublicIds
    );
}
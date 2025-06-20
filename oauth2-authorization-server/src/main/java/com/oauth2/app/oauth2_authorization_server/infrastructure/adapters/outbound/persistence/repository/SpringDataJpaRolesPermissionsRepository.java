package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository;

import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.PermissionEntity;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.RolePermissionId;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.RolesPermissionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaRolesPermissionsRepository extends JpaRepository<RolesPermissionsEntity, RolePermissionId> {

    List<RolesPermissionsEntity> findByRole_IdAndActiveTrue(Integer roleId);

    List<RolesPermissionsEntity> findByRole_Id(Integer roleId);

    Optional<RolesPermissionsEntity> findByRole_IdAndPermission_Id(Integer roleId, Integer permissionId);

    @Query("SELECT rp.permission FROM RolesPermissionsEntity rp " +
           "WHERE rp.role.publicId = :rolePublicId " +
           "AND rp.active = true " +
           "AND rp.permission.active = true")
    List<PermissionEntity> findActivePermissionEntitiesByRolePublicId(@Param("rolePublicId") UUID rolePublicId);
    
    @Query("SELECT rp FROM RolesPermissionsEntity rp " +
           "WHERE rp.role.id = :roleId " +
           "AND rp.permission.publicId IN :permissionPublicIds")
    List<RolesPermissionsEntity> findByRoleIdAndPermissionPublicIds(
            @Param("roleId") Integer roleId,
            @Param("permissionPublicIds") List<UUID> permissionPublicIds
    );
}

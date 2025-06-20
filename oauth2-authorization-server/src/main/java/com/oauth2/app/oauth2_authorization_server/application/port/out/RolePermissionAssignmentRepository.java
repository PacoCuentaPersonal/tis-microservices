package com.oauth2.app.oauth2_authorization_server.application.port.out;

import com.oauth2.app.oauth2_authorization_server.domain.model.Permission;
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RolePermissionAssignmentRepository {

    // Usado por PermissionService
    Set<UUID> findAssignedPermissionPublicIdsByRolePublicId(PublicIdVO rolePublicId);

    // Usados por RolesPermissionsService
    void assignPermissionsToRole(Role role, List<Permission> permissionsToAssign);
    void updatePermissionsForRole(Role role, List<Permission> newPermissions);
    void deactivateAllPermissionsForRole(Role role);
    
    // Podrían necesitarse otros métodos para operaciones más granulares si se refactoriza RolesPermissionsService
    // List<RolesPermissionsEntity> findByRoleId(Integer roleId);
    // List<RolesPermissionsEntity> findByRoleIdAndActiveTrue(Integer roleId);
    // void saveAllRolePermissionEntities(List<RolesPermissionsEntity> assignments);
}

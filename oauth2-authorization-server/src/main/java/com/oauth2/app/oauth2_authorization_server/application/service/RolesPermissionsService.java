package com.oauth2.app.oauth2_authorization_server.application.service;

import com.oauth2.app.oauth2_authorization_server.application.port.in.IRolePermissionsService;
import com.oauth2.app.oauth2_authorization_server.application.port.out.PermissionRepository;
import com.oauth2.app.oauth2_authorization_server.application.port.out.RolePermissionAssignmentRepository;
import com.oauth2.app.oauth2_authorization_server.domain.model.Permission;
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolesPermissionsService implements IRolePermissionsService {

    private final RolePermissionAssignmentRepository rolePermissionAssignmentRepository;
    private final PermissionRepository permissionRepository; // To validate and fetch domain Permissions

    @Override
    @Transactional
    public void updateRolePermissions(List<UUID> permissionPublicIds, Role role) {
        log.info("Updating permissions for role '{}' ({}). New permission public ID count: {}",
                role.getName(), role.getPublicId().value(), permissionPublicIds.size());

        if (role.getPublicId() == null) {
            throw new IllegalArgumentException("Role public ID cannot be null for updating permissions.");
        }

        List<Permission> newPermissions = Collections.emptyList();
        if (permissionPublicIds != null && !permissionPublicIds.isEmpty()) {
            newPermissions = validateAndGetPermissionsByPublicIds(permissionPublicIds);
        }
        
        rolePermissionAssignmentRepository.updatePermissionsForRole(role, newPermissions);
        log.info("Successfully updated permissions for role '{}'", role.getName());
    }

    @Override
    @Transactional
    public void assignPermissionsToRole(List<UUID> permissionPublicIds, Role role) {
        log.info("Assigning initial permissions for new role '{}' ({})", role.getName(), role.getPublicId().value());

        if (role.getPublicId() == null) {
            throw new IllegalArgumentException("Role public ID cannot be null for assigning permissions.");
        }

        if (permissionPublicIds == null || permissionPublicIds.isEmpty()) {
            log.info("No permissions to assign to new role '{}'", role.getName());
            return;
        }

        List<Permission> permissionsToAssign = validateAndGetPermissionsByPublicIds(permissionPublicIds);
        rolePermissionAssignmentRepository.assignPermissionsToRole(role, permissionsToAssign);
        log.info("Assigned {} permissions to new role '{}'", permissionsToAssign.size(), role.getName());
    }

    @Override
    @Transactional
    public void deactivateAllRolePermissions(Role role) {
        log.info("Deactivating all permissions for role '{}' ({})", role.getName(), role.getPublicId().value());
        if (role.getPublicId() == null) {
            throw new IllegalArgumentException("Role public ID cannot be null for deactivating permissions.");
        }
        rolePermissionAssignmentRepository.deactivateAllPermissionsForRole(role);
        log.info("Successfully deactivated all permissions for role '{}'", role.getName());
    }

    private List<Permission> validateAndGetPermissionsByPublicIds(List<UUID> permissionPublicIds) {
        List<Permission> permissions = permissionRepository.findAllByActiveTrueAndPublicIdIn(permissionPublicIds);
        if (permissions.size() != permissionPublicIds.size()) {
            List<UUID> foundIds = permissions.stream().map(p -> p.getPublicId().value()).collect(Collectors.toList());
            List<UUID> missingIds = permissionPublicIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
            log.error("Missing or inactive permissions for public IDs: {}", missingIds);
            throw new IllegalArgumentException("Some permissions were not found or are inactive: " + missingIds);
        }
        return permissions;
    }
}

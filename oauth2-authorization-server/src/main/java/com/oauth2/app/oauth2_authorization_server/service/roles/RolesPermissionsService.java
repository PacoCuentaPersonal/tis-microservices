package com.oauth2.app.oauth2_authorization_server.service.roles;

import com.oauth2.app.oauth2_authorization_server.models.entity.Permissions;
import com.oauth2.app.oauth2_authorization_server.models.entity.RolePermissionId;
import com.oauth2.app.oauth2_authorization_server.models.entity.Roles;
import com.oauth2.app.oauth2_authorization_server.models.entity.RolesPermissions;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaPermissionRepository;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaRolesPermissionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolesPermissionsService implements IRolePermissionsService{

    private final JpaRolesPermissionsRepository rpRepository;
    private final JpaPermissionRepository permissionRepository;

    @Transactional
    public void updateRolePermissions(List<UUID> permissionPublicIds, Roles role) {
        log.info("Updating permissions for role '{}'. New permission count: {}",
                role.getName(), permissionPublicIds.size());

        // Si la lista está vacía, es más eficiente usar deactivateAll
        if (permissionPublicIds.isEmpty()) {
            log.info("Empty permission list - deactivating all permissions for role '{}'", role.getName());
            deactivateAllRolePermissions(role);
            return;
        }

        // Validar y obtener permisos
        List<Permissions> newPermissions = validateAndGetPermissions(permissionPublicIds);

        // Obtener estado actual
        List<RolesPermissions> currentPermissions = rpRepository.findByRoleId(role.getId());

        // Verificar si hay cambios reales antes de procesar
        Set<Integer> currentActiveIds = currentPermissions.stream()
                .filter(RolesPermissions::getActive)
                .map(rp -> rp.getPermission().getId())
                .collect(Collectors.toSet());

        Set<Integer> newPermissionIds = extractPermissionIds(newPermissions);

        // Si los conjuntos son iguales, no hay cambios
        if (currentActiveIds.equals(newPermissionIds)) {
            log.info("No permission changes detected for role '{}'. Skipping update.", role.getName());
            return;
        }

        Map<Integer, RolesPermissions> existingByPermissionId = mapByPermissionId(currentPermissions);

        // Procesar cambios
        List<RolesPermissions> toUpdate = processPermissionChanges(
                currentPermissions,
                existingByPermissionId,
                newPermissionIds,
                newPermissions,
                role
        );

        // Guardar cambios
        saveChanges(toUpdate, role.getName());
    }

    @Transactional
    public void assignPermissionsToRole(List<UUID> permissionPublicIds, Roles role) {
        log.info("Initial permission assignment for new role '{}'", role.getName());

        if (permissionPublicIds.isEmpty()) {
            log.info("No permissions to assign to new role '{}'", role.getName());
            return;
        }

        List<Permissions> permissions = validateAndGetPermissions(permissionPublicIds);
        List<RolesPermissions> rolePermissions = createRolePermissions(permissions, role);

        rpRepository.saveAll(rolePermissions);
        log.info("Assigned {} permissions to new role '{}'", rolePermissions.size(), role.getName());
    }

    @Transactional
    public void deactivateAllRolePermissions(Roles role) {
        log.info("Deactivating all permissions for role '{}'", role.getName());

        List<RolesPermissions> activePermissions = rpRepository
                .findByRoleIdAndActiveTrue(role.getId());

        if (activePermissions.isEmpty()) {
            log.info("No active permissions found for role '{}'", role.getName());
            return;
        }

        activePermissions.forEach(rp -> rp.setActive(false));
        rpRepository.saveAll(activePermissions);
        log.info("Deactivated {} permissions for role '{}'",
                activePermissions.size(), role.getName());
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private List<Permissions> validateAndGetPermissions(List<UUID> permissionPublicIds) {
        List<Permissions> permissions = permissionRepository
                .findAllByActiveTrueAndPublicIdIn(permissionPublicIds);

        if (permissions.size() != permissionPublicIds.size()) {
            Set<UUID> foundIds = permissions.stream()
                    .map(Permissions::getPublicId)
                    .collect(Collectors.toSet());

            Set<UUID> missingIds = new HashSet<>(permissionPublicIds);
            missingIds.removeAll(foundIds);

            log.error("Missing or inactive permissions: {}", missingIds);
            throw new IllegalArgumentException("Some permissions were not found or are inactive: " + missingIds);
        }

        return permissions;
    }

    private Map<Integer, RolesPermissions> mapByPermissionId(List<RolesPermissions> permissions) {
        return permissions.stream()
                .collect(Collectors.toMap(
                        rp -> rp.getPermission().getId(),
                        Function.identity()
                ));
    }

    private Set<Integer> extractPermissionIds(List<Permissions> permissions) {
        return permissions.stream()
                .map(Permissions::getId)
                .collect(Collectors.toSet());
    }

    private List<RolesPermissions> processPermissionChanges(
            List<RolesPermissions> currentPermissions,
            Map<Integer, RolesPermissions> existingByPermissionId,
            Set<Integer> newPermissionIds,
            List<Permissions> newPermissions,
            Roles role) {

        List<RolesPermissions> toUpdate = new ArrayList<>();

        // Desactivar permisos que ya no están
        toUpdate.addAll(deactivateRemovedPermissions(currentPermissions, newPermissionIds, role));

        // Activar o crear nuevos permisos
        toUpdate.addAll(activateOrCreatePermissions(newPermissions, existingByPermissionId, role));

        return toUpdate;
    }

    private List<RolesPermissions> deactivateRemovedPermissions(
            List<RolesPermissions> currentPermissions,
            Set<Integer> newPermissionIds,
            Roles role) {

        List<RolesPermissions> toDeactivate = new ArrayList<>();

        for (RolesPermissions existing : currentPermissions) {
            if (existing.getActive() && !newPermissionIds.contains(existing.getPermission().getId())) {
                existing.setActive(false);
                toDeactivate.add(existing);
                log.debug("Deactivating permission '{}' for role '{}'",
                        existing.getPermission().getName(), role.getName());
            }
        }

        return toDeactivate;
    }

    private List<RolesPermissions> activateOrCreatePermissions(
            List<Permissions> newPermissions,
            Map<Integer, RolesPermissions> existingByPermissionId,
            Roles role) {

        List<RolesPermissions> toUpdate = new ArrayList<>();

        for (Permissions permission : newPermissions) {
            RolesPermissions existing = existingByPermissionId.get(permission.getId());

            if (existing != null) {
                if (!existing.getActive()) {
                    existing.setActive(true);
                    toUpdate.add(existing);
                    log.debug("Reactivating permission '{}' for role '{}'",
                            permission.getName(), role.getName());
                }
            } else {
                RolesPermissions newRp = createRolePermission(permission, role);
                toUpdate.add(newRp);
                log.debug("Adding new permission '{}' to role '{}'",
                        permission.getName(), role.getName());
            }
        }

        return toUpdate;
    }

    private List<RolesPermissions> createRolePermissions(List<Permissions> permissions, Roles role) {
        return permissions.stream()
                .map(permission -> createRolePermission(permission, role))
                .collect(Collectors.toList());
    }

    private RolesPermissions createRolePermission(Permissions permission, Roles role) {
        RolePermissionId id = new RolePermissionId();
        id.setRoleId(role.getId());
        id.setPermissionId(permission.getId());

        return RolesPermissions.builder()
                .id(id)
                .role(role)
                .permission(permission)
                .active(true)
                .build();
    }

    private void saveChanges(List<RolesPermissions> toUpdate, String roleName) {
        if (!toUpdate.isEmpty()) {
            rpRepository.saveAll(toUpdate);
            log.info("Updated {} permission assignments for role '{}'",
                    toUpdate.size(), roleName);
        } else {
            log.debug("No permission changes to save for role '{}'", roleName);
        }
    }
}
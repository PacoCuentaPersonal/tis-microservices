package com.oauth2.app.oauth2_authorization_server.service.roles;

import com.oauth2.app.oauth2_authorization_server.dto.request.roles.ActivePermissionsToRoleRequest;
import com.oauth2.app.oauth2_authorization_server.dto.response.PermissionAssignmentResponse;
import com.oauth2.app.oauth2_authorization_server.models.entity.Permissions;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaPermissionRepository;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaRolesPermissions;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionService implements IPermissionsService {

    private final JpaPermissionRepository permissionRepository;
    private final JpaRolesPermissions rolesPermissionsRepository;

    public PermissionService(JpaPermissionRepository permissionRepository,
                             JpaRolesPermissions rolesPermissionsRepository) {
        this.permissionRepository = permissionRepository;
        this.rolesPermissionsRepository = rolesPermissionsRepository;
    }

    @Override
    public List<ActivePermissionsToRoleRequest> getActivePermissionsToRole(UUID rolePublicId) {
        return getPermissionsWithAssignmentStatus(rolePublicId)
                .stream()
                .map(p -> new ActivePermissionsToRoleRequest(
                        p.publicId(),
                        p.code(),
                        p.name(),
                        p.assigned()
                ))
                .collect(Collectors.toList());
    }

    public List<PermissionAssignmentResponse> getPermissionsWithAssignmentStatus(UUID rolePublicId) {
        // Obtener IDs de permisos asignados al rol
        Set<UUID> assignedPermissionIds = rolesPermissionsRepository
                .findPermissionsByRolePublicId(rolePublicId)
                .stream()
                .map(Permissions::getPublicId)
                .collect(Collectors.toSet());

        // Obtener TODOS los permisos activos y marcar los asignados
        return permissionRepository.findAllByActiveTrue()
                .stream()
                .map(permission -> new PermissionAssignmentResponse(
                        permission.getPublicId(),
                        permission.getCode(),
                        permission.getName(),
                        assignedPermissionIds.contains(permission.getPublicId())
                ))
                .sorted(Comparator.comparing(PermissionAssignmentResponse::name))
                .collect(Collectors.toList());
    }

    public Permissions getPermissionByPublicId(String name) {
        return permissionRepository.findByActiveTrueAndName(name)
                .orElseThrow(() -> new NoSuchElementException("Permission not found with name: " + name));
    }

    public List<Permissions> getAllPermissionsByPublicIdsIn(List<UUID> publicIds) {
        if (publicIds == null || publicIds.isEmpty()) {
            return Collections.emptyList();
        }
        return permissionRepository.findAllByActiveTrueAndPublicIdIn(publicIds);
    }

    public boolean checkDuplicateName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return permissionRepository.findByActiveTrueAndName(name).isPresent();
    }

    public boolean checkPermissions(List<UUID> publicIds) {
        if (publicIds == null || publicIds.isEmpty()) {
            return false;
        }
        return  permissionRepository.existsByActiveTrueAndPublicIdIn(publicIds);
    }


}
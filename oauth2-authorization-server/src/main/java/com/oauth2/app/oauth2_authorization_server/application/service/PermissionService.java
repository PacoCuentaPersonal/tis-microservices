package com.oauth2.app.oauth2_authorization_server.application.service;

import com.oauth2.app.oauth2_authorization_server.application.port.in.IPermissionsService;
import com.oauth2.app.oauth2_authorization_server.application.port.out.PermissionRepository;
import com.oauth2.app.oauth2_authorization_server.application.port.out.RolePermissionAssignmentRepository; 
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles.ActivePermissionsToRoleRequest;
// Corrected DTO response import
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.PermissionAssignmentResponse; 
import com.oauth2.app.oauth2_authorization_server.domain.model.Permission;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionsService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionAssignmentRepository rolePermissionAssignmentRepository; 

    @Override
    public List<ActivePermissionsToRoleRequest> getActivePermissionsToRole(UUID rolePublicId) {
        return getPermissionsWithAssignmentStatus(rolePublicId)
                .stream()
                .map(p -> new ActivePermissionsToRoleRequest(
                        p.publicId().value(),
                        p.code(),
                        p.name(),
                        p.assigned()
                ))
                .collect(Collectors.toList());
    }

    public List<PermissionAssignmentResponse> getPermissionsWithAssignmentStatus(UUID rolePublicId) {
        Set<UUID> assignedPermissionPublicIds = rolePermissionAssignmentRepository
                .findAssignedPermissionPublicIdsByRolePublicId(new PublicIdVO(rolePublicId));

        List<Permission> allActivePermissions = permissionRepository.findAllByActiveTrue();

        return allActivePermissions.stream()
                .map(permission -> new PermissionAssignmentResponse(
                        permission.getPublicId(),
                        permission.getCode(),
                        permission.getName(),
                        assignedPermissionPublicIds.contains(permission.getPublicId().value())
                ))
                .sorted(Comparator.comparing(PermissionAssignmentResponse::name))
                .collect(Collectors.toList());
    }

    public Permission getPermissionByPublicId(UUID publicId) {
        return permissionRepository.findByActiveTrueAndPublicId(new PublicIdVO(publicId))
                .orElseThrow(() -> new NoSuchElementException("Permission not found with publicId: " + publicId));
    }

    public List<Permission> getAllPermissionsByPublicIdsIn(List<UUID> publicIds) {
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
            return true; 
        }
        return permissionRepository.existsByActiveTrueAndPublicIdIn(publicIds);
    }
}

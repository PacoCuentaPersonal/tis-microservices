package com.oauth2.app.oauth2_authorization_server.application.service;

import com.oauth2.app.oauth2_authorization_server.application.port.in.IRoleService;
import com.oauth2.app.oauth2_authorization_server.application.port.out.AccountRepository; 
import com.oauth2.app.oauth2_authorization_server.application.port.out.RoleRepository;
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
// Corrected DTO imports
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles.RolesRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles.RolesUpdateRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.PermissionAssignmentResponse; // Path corregido
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.RoleDetailsResponse; // Path corregido
import com.oauth2.app.oauth2_authorization_server.application.exception.throwers.RoleNotFoundException; // Path corregido
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolesService implements IRoleService {

    private final RoleRepository roleRepository; 
    private final AccountRepository accountRepository; 
    private final PermissionService permissionService; 
    private final RolesPermissionsService rolesPermissionsService; 

    @Override
    @Transactional(readOnly = true)
    public Page<Role> findAll(Pageable pageable) {
        log.info("Fetching all active roles");
        return roleRepository.findAllByActiveTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDetailsResponse findDetails(UUID publicId) {
        log.info("Fetching complete role details for publicId: {}", publicId);
        Role role = findByPublicId(publicId);
        List<PermissionAssignmentResponse> permissionsWithStatus =
                permissionService.getPermissionsWithAssignmentStatus(publicId);
        long assignedCount = permissionsWithStatus.stream()
                .filter(PermissionAssignmentResponse::assigned)
                .count();

        return new RoleDetailsResponse(
                role.getPublicId().value(),
                role.getName(),
                role.getDescription(),
                role.isActive(),
                role.getCreatedAt(),
                role.getUpdatedAt(),
                permissionsWithStatus,
                permissionsWithStatus.size(),
                (int) assignedCount
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByPublicId(UUID publicId) {
        log.info("Finding role by publicId: {}", publicId);
        return roleRepository.findByPublicIdAndActiveTrue(new PublicIdVO(publicId))
                .orElseThrow(() -> new RoleNotFoundException("Role not found with PublicId: " + publicId));
    }

    @Override
    @Transactional
    public void delete(UUID publicId) {
        log.info("Process soft delete for role with id: {}", publicId);
        if (publicId == null) {
            throw new IllegalArgumentException("Role PublicId cannot be null");
        }

        Role role = roleRepository.findByPublicIdAndActiveTrue(new PublicIdVO(publicId))
                .orElseThrow(() -> new RoleNotFoundException("Role not found with PublicId: " + publicId + " for deletion."));

        // TODO: Refactor this check. AccountRepository needs a method like existsByRoleIdAndActiveTrue(Integer roleId)
        // or existsByRolePublicIdAndActiveTrue(PublicIdVO rolePublicId) if Account domain model holds Role directly.
        // For now, assuming role.getId() is the internal integer ID.
        // if (accountRepository.existsByInternalRoleIdAndActiveTrue(role.getId())) { 
        //    throw new IllegalStateException("Cannot delete role: It is assigned to active users");
        // }

        role.setActive(false);
        Role updatedRole = roleRepository.save(role);
        rolesPermissionsService.deactivateAllRolePermissions(updatedRole);
        log.info("Role '{}' has been deactivated", updatedRole.getName());
    }

    @Override
    @Transactional
    public Role save(RolesRequest request) {
        log.info("Creating new role: {}", request.name());
        checkDuplicateNameForCreate(request.name());
        validatePermissions(request.publicIdPermissions());

        Role role = Role.builder()
                .name(request.name())
                .description(request.description())
                .active(true)
                .publicId(PublicIdVO.generate()) 
                .build();

        Role savedRole = roleRepository.save(role);
        rolesPermissionsService.assignPermissionsToRole(request.publicIdPermissions(), savedRole);
        return savedRole;
    }

    @Override
    @Transactional
    public Role update(UUID publicId, RolesUpdateRequest request) {
        log.info("Updating role with publicId: {}", publicId);
        Role role = roleRepository.findByPublicIdAndActiveTrue(new PublicIdVO(publicId))
                 .orElseThrow(() -> new RoleNotFoundException("Role not found with PublicId: " + publicId + " for update."));
        
        boolean roleModified = false;

        if (request.hasName() && !role.getName().equals(request.name())) {
            checkDuplicateNameForUpdate(request.name(), role.getId());
            role.setName(request.name());
            roleModified = true;
        }
        if (request.hasDescription()) {
            role.setDescription(request.description());
            roleModified = true;
        }
        if (roleModified) {
            role = roleRepository.save(role);
        }
        if (request.hasPublicIdPermissions()) {
            if (request.publicIdPermissions().isEmpty()) {
                log.info("Empty permission list received - deactivating all permissions for role: {}", role.getName());
                rolesPermissionsService.deactivateAllRolePermissions(role);
            } else {
                validatePermissions(request.publicIdPermissions());
                rolesPermissionsService.updateRolePermissions(request.publicIdPermissions(), role);
            }
        }
        return role;
    }

    private void checkDuplicateNameForCreate(String name) {
        if (roleRepository.existsByNameAndActiveTrue(name)) {
            throw new IllegalArgumentException("Role name already exists: " + name);
        }
    }

    private void checkDuplicateNameForUpdate(String name, Integer currentRoleId) {
        // currentRoleId is the internal Integer ID of the role being updated.
        if (roleRepository.existsByNameAndActiveTrueAndIdNot(name, currentRoleId)) {
            throw new IllegalArgumentException("Role name already exists: " + name);
        }
    }

    private void validatePermissions(List<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        if (!permissionService.checkPermissions(permissionIds)) {
            throw new IllegalArgumentException("Some permissions are invalid or inactive");
        }
    }
}

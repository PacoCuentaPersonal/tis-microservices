package com.oauth2.app.oauth2_authorization_server.service.roles;

import com.oauth2.app.oauth2_authorization_server.dto.request.roles.RolesUpdateRequest;
import com.oauth2.app.oauth2_authorization_server.dto.response.PermissionAssignmentResponse;
import com.oauth2.app.oauth2_authorization_server.dto.response.RoleDetailsResponse;
import com.oauth2.app.oauth2_authorization_server.exception.throwers.RoleNotFoundException;
import com.oauth2.app.oauth2_authorization_server.models.entity.Roles;
import com.oauth2.app.oauth2_authorization_server.dto.request.roles.RolesRequest;
import com.oauth2.app.oauth2_authorization_server.dto.response.RolesResponse;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaAccountRepository;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaRolesRepository;
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

    private final JpaRolesRepository rolesRepository;
    private final JpaAccountRepository jpaAccountRepository;
    private final PermissionService permissionService;
    private final RolesPermissionsService rolesPermissionsService;

    @Transactional(readOnly = true)
    public Page<Roles> findAll(Pageable pageable) {
        log.info("Fetching all active roles");
        return rolesRepository.findAllByActiveTrue(pageable);
    }

    @Transactional(readOnly = true)
    public RoleDetailsResponse findDetails(UUID publicId) {
        log.info("Fetching complete role details for publicId: {}", publicId);

        Roles role = findRoleByPublicId(publicId);
        List<PermissionAssignmentResponse> permissionsWithStatus =
                permissionService.getPermissionsWithAssignmentStatus(publicId);
        long assignedCount = permissionsWithStatus.stream()
                .filter(PermissionAssignmentResponse::assigned)
                .count();

        return new RoleDetailsResponse(
                role.getPublicId(),
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

    public Roles findByPublicId(UUID publicId) {
        log.info("Finding role by publicId: {}", publicId);
        return rolesRepository.findByPublicIdAndActiveTrue(publicId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with PublicId: " + publicId));
    }

    @Transactional
    public void delete(UUID publicId) {
        log.info("Process soft delete for role with id: {}", publicId);

        if (publicId == null) {
            throw new IllegalArgumentException("Role PublicId cannot be null");
        }

        if (jpaAccountRepository.existsByPublicIdAndActiveTrue(publicId)) {
            throw new IllegalStateException("Cannot delete role: It is assigned to users");
        }

        Roles role = rolesRepository.findByPublicIdAndActiveTrue(publicId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with PublicId: " + publicId));

        role.setActive(false);
        rolesRepository.save(role);

        rolesPermissionsService.deactivateAllRolePermissions(role);

        log.info("Role '{}' has been deactivated", role.getName());
    }

    @Transactional(readOnly = false)
    public Roles save(RolesRequest request) {
        log.info("Creating new role: {}", request.name());

        checkDuplicateNameForCreate(request.name());
        validatePermissions(request.publicIdPermissions());

        Roles role = new Roles();
        role.setName(request.name());
        role.setDescription(request.description());
        role.setActive(true);

        Roles savedRole = rolesRepository.save(role);

        rolesPermissionsService.assignPermissionsToRole(request.publicIdPermissions(), savedRole);

        return savedRole;
    }

    @Transactional(readOnly = false)
    public Roles update(UUID publicId, RolesUpdateRequest request) {
        log.info("Updating role with publicId: {}", publicId);

        Roles role = findRoleByPublicId(publicId);
        boolean roleModified = false;

        // Actualizar nombre si es necesario
        if (request.hasName() && !role.getName().equals(request.name())) {
            checkDuplicateNameForUpdate(request.name(), role.getId());
            role.setName(request.name());
            roleModified = true;
        }

        // Actualizar descripción si es necesario
        if (request.hasDescription()) {
            role.setDescription(request.description());
            roleModified = true;
        }

        // Guardar cambios en el rol si hubo modificaciones
        if (roleModified) {
            role = rolesRepository.save(role);
        }

        // Actualizar permisos si se proporcionaron
        if (request.hasPublicIdPermissions()) {
            // Caso especial: lista vacía = quitar todos los permisos
            if (request.publicIdPermissions().isEmpty()) {
                log.info("Empty permission list received - deactivating all permissions for role: {}", role.getName());
                rolesPermissionsService.deactivateAllRolePermissions(role);
            } else {
                // Validar permisos antes de actualizar
                validatePermissions(request.publicIdPermissions());
                rolesPermissionsService.updateRolePermissions(request.publicIdPermissions(), role);
            }
        }

        return role;
    }

    private void checkDuplicateNameForCreate(String name) {
        if (rolesRepository.existsByNameAndActiveTrue(name)) {
            throw new IllegalArgumentException("Role name already exists: " + name);
        }
    }

    private void checkDuplicateNameForUpdate(String name, Integer currentRoleId) {
        if (rolesRepository.existsByNameAndActiveTrueAndIdNot(name, currentRoleId)) {
            throw new IllegalArgumentException("Role name already exists: " + name);
        }
    }

    private Roles findRoleByPublicId(UUID publicId) {
        if (publicId == null) {
            throw new IllegalArgumentException("Role publicId cannot be null");
        }

        return rolesRepository.findByPublicIdAndActiveTrue(publicId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with publicId: " + publicId));
    }

    private void validatePermissions(List<UUID> permissionIds) {
        // No validar si la lista está vacía (es un caso válido)
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }

        if (!permissionService.checkPermissions(permissionIds)) {
            throw new IllegalArgumentException("Some permissions are invalid or inactive");
        }
    }
}
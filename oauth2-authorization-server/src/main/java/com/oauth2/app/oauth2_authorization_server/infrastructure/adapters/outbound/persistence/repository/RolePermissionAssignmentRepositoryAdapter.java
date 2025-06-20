package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository;

import com.oauth2.app.oauth2_authorization_server.application.port.out.RolePermissionAssignmentRepository;
import com.oauth2.app.oauth2_authorization_server.domain.model.Permission;
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.PermissionEntity;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.RolePermissionId;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.RolesEntity;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.RolesPermissionsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolePermissionAssignmentRepositoryAdapter implements RolePermissionAssignmentRepository {

    private final SpringDataJpaRolesPermissionsRepository springDataRepo;
    private final SpringDataJpaPermissionRepository permissionSpringDataRepo; // Para buscar PermissionEntity por PublicId
    private final SpringDataJpaRolesRepository roleSpringDataRepo; // Para buscar RolesEntity por PublicId

    @Override
    public Set<UUID> findAssignedPermissionPublicIdsByRolePublicId(PublicIdVO rolePublicId) {
        return springDataRepo.findActivePermissionEntitiesByRolePublicId(rolePublicId.value())
                .stream()
                .map(PermissionEntity::getPublicId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void assignPermissionsToRole(Role role, List<Permission> permissionsToAssign) {
        RolesEntity roleEntity = roleSpringDataRepo.findByPublicIdAndActiveTrue(role.getPublicId().value())
            .orElseThrow(() -> new IllegalArgumentException("Role not found for assignment: " + role.getPublicId().value()));

        List<RolesPermissionsEntity> newAssignments = permissionsToAssign.stream()
                .map(domainPermission -> {
                    PermissionEntity permissionEntity = permissionSpringDataRepo.findByActiveTrueAndPublicId(domainPermission.getPublicId().value())
                            .orElseThrow(() -> new IllegalArgumentException("Permission not found or inactive: " + domainPermission.getPublicId().value()));
                    
                    RolePermissionId id = new RolePermissionId(permissionEntity.getId(), roleEntity.getId());
                    return RolesPermissionsEntity.builder()
                            .id(id)
                            .role(roleEntity)
                            .permission(permissionEntity)
                            .active(true)
                            .build();
                })
                .collect(Collectors.toList());
        if (!newAssignments.isEmpty()) {
            springDataRepo.saveAll(newAssignments);
        }
    }
    
    @Override
    @Transactional
    public void updatePermissionsForRole(Role role, List<Permission> newPermissions) {
        RolesEntity roleEntity = roleSpringDataRepo.findByPublicIdAndActiveTrue(role.getPublicId().value())
            .orElseThrow(() -> new IllegalArgumentException("Role not found for update: " + role.getPublicId().value()));

        List<RolesPermissionsEntity> currentAssignments = springDataRepo.findByRole_Id(roleEntity.getId());
        Map<Integer, RolesPermissionsEntity> currentAssignmentsMap = currentAssignments.stream()
                .collect(Collectors.toMap(rp -> rp.getPermission().getId(), Function.identity()));

        Set<Integer> newPermissionEntityIds = newPermissions.stream()
                .map(p -> permissionSpringDataRepo.findByActiveTrueAndPublicId(p.getPublicId().value())
                         .orElseThrow(() -> new IllegalArgumentException("Permission not found or inactive: " + p.getPublicId().value()))
                         .getId())
                .collect(Collectors.toSet());

        List<RolesPermissionsEntity> toSave = new ArrayList<>();

        for (RolesPermissionsEntity currentAssignment : currentAssignments) {
            if (currentAssignment.getActive() && !newPermissionEntityIds.contains(currentAssignment.getPermission().getId())) {
                currentAssignment.setActive(false);
                toSave.add(currentAssignment);
            }
        }

        for (Permission domainPermission : newPermissions) {
            PermissionEntity permissionEntity = permissionSpringDataRepo.findByActiveTrueAndPublicId(domainPermission.getPublicId().value())
                 .orElseThrow(() -> new IllegalArgumentException("Permission not found or inactive: " + domainPermission.getPublicId().value()));

            RolesPermissionsEntity existingAssignment = currentAssignmentsMap.get(permissionEntity.getId());
            if (existingAssignment != null) {
                if (!existingAssignment.getActive()) {
                    existingAssignment.setActive(true);
                    toSave.add(existingAssignment);
                }
            } else {
                RolePermissionId id = new RolePermissionId(permissionEntity.getId(), roleEntity.getId());
                toSave.add(RolesPermissionsEntity.builder()
                        .id(id)
                        .role(roleEntity)
                        .permission(permissionEntity)
                        .active(true)
                        .build());
            }
        }
        if (!toSave.isEmpty()) {
            springDataRepo.saveAll(toSave);
        }
    }

    @Override
    @Transactional
    public void deactivateAllPermissionsForRole(Role role) {
         RolesEntity roleEntity = roleSpringDataRepo.findByPublicIdAndActiveTrue(role.getPublicId().value())
            .orElseThrow(() -> new IllegalArgumentException("Role not found for deactivation: " + role.getPublicId().value()));

        List<RolesPermissionsEntity> activeAssignments = springDataRepo.findByRole_IdAndActiveTrue(roleEntity.getId());
        if (!activeAssignments.isEmpty()) {
            activeAssignments.forEach(assignment -> assignment.setActive(false));
            springDataRepo.saveAll(activeAssignments);
        }
    }
}

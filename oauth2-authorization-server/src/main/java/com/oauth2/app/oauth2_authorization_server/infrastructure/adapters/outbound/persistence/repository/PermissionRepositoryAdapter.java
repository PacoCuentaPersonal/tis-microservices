package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository;

import com.oauth2.app.oauth2_authorization_server.application.port.out.PermissionRepository;
import com.oauth2.app.oauth2_authorization_server.domain.model.Permission;
import com.oauth2.app.oauth2_authorization_server.domain.model.Submodule;
import com.oauth2.app.oauth2_authorization_server.domain.model.Module; // For submodule mapping
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.PermissionEntity;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.SubmoduleEntity;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.ModuleEntity; // For submodule mapping
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final SpringDataJpaPermissionRepository springDataJpaRepository;

    @Override
    public Optional<Permission> findByActiveTrueAndName(String name) {
        return springDataJpaRepository.findByActiveTrueAndName(name).map(this::mapEntityToDomain);
    }

    @Override
    public List<Permission> findAllByActiveTrue() {
        return springDataJpaRepository.findAllByActiveTrue().stream()
                .map(this::mapEntityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByActiveTrueAndPublicIdIn(List<UUID> publicIds) {
        // La interfaz del puerto espera List<UUID>, así que la pasamos directamente.
        return springDataJpaRepository.existsByActiveTrueAndPublicIdIn(publicIds);
    }

    @Override
    public List<Permission> findAllByActiveTrueAndPublicIdIn(List<UUID> publicIds) {
        return springDataJpaRepository.findAllByActiveTrueAndPublicIdIn(publicIds).stream()
                .map(this::mapEntityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Permission> findByActiveTrueAndPublicId(PublicIdVO publicId) {
        return springDataJpaRepository.findByActiveTrueAndPublicId(publicId.value()).map(this::mapEntityToDomain);
    }

    private Permission mapEntityToDomain(PermissionEntity entity) {
        if (entity == null) return null;
        return Permission.builder()
                .id(entity.getId())
                .publicId(new PublicIdVO(entity.getPublicId()))
                .code(entity.getCode())
                .name(entity.getName())
                .active(entity.getActive())
                .submodule(mapSubmoduleEntityToDomain(entity.getSubmodule()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // El mapeo de PermissionEntity a Permission necesita el mapeo de SubmoduleEntity a Submodule
    private Submodule mapSubmoduleEntityToDomain(SubmoduleEntity entity) {
        if (entity == null) return null;
        return Submodule.builder()
                .id(entity.getId())
                .publicId(new PublicIdVO(entity.getPublicId()))
                .code(entity.getCode())
                .name(entity.getName())
                .active(entity.getActive())
                .module(mapModuleEntityToDomain(entity.getModule())) // Mapeo de Module anidado
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // Mapeo de ModuleEntity a Module (necesario para Submodule)
    private Module mapModuleEntityToDomain(ModuleEntity entity) {
        if (entity == null) return null;
        return Module.builder()
            .id(entity.getId())
            .publicId(new PublicIdVO(entity.getPublicId()))
            .code(entity.getCode())
            .name(entity.getName())
            .active(entity.getActive())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}

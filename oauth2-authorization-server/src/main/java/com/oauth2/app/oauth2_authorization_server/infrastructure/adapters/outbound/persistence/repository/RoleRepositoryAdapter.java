package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository;

import com.oauth2.app.oauth2_authorization_server.application.port.out.RoleRepository;
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.RolesEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final SpringDataJpaRolesRepository springDataJpaRepository;

    @Override
    public Optional<Role> findByNameAndActiveTrue(String name) {
        return springDataJpaRepository.findByNameAndActiveTrue(name).map(this::mapEntityToDomain);
    }

    @Override
    public boolean existsByNameAndActiveTrue(String name) {
        return springDataJpaRepository.existsByNameAndActiveTrue(name);
    }

    @Override
    public Optional<Role> findByPublicIdAndActiveTrue(PublicIdVO publicId) {
        return springDataJpaRepository.findByPublicIdAndActiveTrue(publicId.value()).map(this::mapEntityToDomain);
    }

    @Override
    public Page<Role> findAllByActiveTrue(Pageable pageable) {
        return springDataJpaRepository.findAllByActiveTrue(pageable).map(this::mapEntityToDomain);
    }

    @Override
    public boolean existsByNameAndActiveTrueAndIdNot(String name, Integer id) {
        return springDataJpaRepository.existsByNameAndActiveTrueAndIdNot(name, id);
    }

    @Override
    public boolean existsByPublicIdAndActiveTrue(PublicIdVO publicId) {
        return springDataJpaRepository.existsByPublicIdAndActiveTrue(publicId.value());
    }
    
    @Override
    public Optional<Role> findById(Integer id) {
        return springDataJpaRepository.findById(id).map(this::mapEntityToDomain);
    }

    @Override
    public Role save(Role role) {
        RolesEntity entity = mapDomainToEntity(role);
        RolesEntity savedEntity = springDataJpaRepository.save(entity);
        return mapEntityToDomain(savedEntity);
    }

    private Role mapEntityToDomain(RolesEntity entity) {
        if (entity == null) return null;
        return Role.builder()
                .id(entity.getId())
                .publicId(new PublicIdVO(entity.getPublicId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private RolesEntity mapDomainToEntity(Role domain) {
        if (domain == null) return null;
        RolesEntity entity = new RolesEntity();
        entity.setId(domain.getId());
        if (domain.getPublicId() != null) {
             entity.setPublicId(domain.getPublicId().value());
        } else if (domain.getId() == null) {
            // Potentially generate UUID if it's a new domain object without publicId and it's expected to be set before save by adapter
            // However, @UuidGenerator on RolesEntity should handle this if publicId is null on persist
        }
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt()); 
        entity.setUpdatedAt(domain.getUpdatedAt()); 
        return entity;
    }
}

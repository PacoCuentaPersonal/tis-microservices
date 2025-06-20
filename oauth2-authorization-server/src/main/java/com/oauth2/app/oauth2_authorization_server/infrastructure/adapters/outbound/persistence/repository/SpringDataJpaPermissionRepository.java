package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository;

import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaPermissionRepository extends JpaRepository<PermissionEntity, Integer> { 
    Optional<PermissionEntity> findByActiveTrueAndName(String name);
    List<PermissionEntity> findAllByActiveTrue();
    boolean existsByActiveTrueAndPublicIdIn(List<UUID> publicIds);
    List<PermissionEntity> findAllByActiveTrueAndPublicIdIn(List<UUID> publicIds);
    Optional<PermissionEntity> findByActiveTrueAndPublicId(UUID publicId);
}

package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository;

import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.RolesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaRolesRepository extends JpaRepository<RolesEntity, Integer> {
    Optional<RolesEntity> findByNameAndActiveTrue(String name);
    boolean existsByNameAndActiveTrue(String name);
    Optional<RolesEntity> findByPublicIdAndActiveTrue(UUID publicId);
    Page<RolesEntity> findAllByActiveTrue(Pageable pageable);
    boolean existsByNameAndActiveTrueAndIdNot(String name, Integer id);
    boolean existsByPublicIdAndActiveTrue(UUID publicId);
}

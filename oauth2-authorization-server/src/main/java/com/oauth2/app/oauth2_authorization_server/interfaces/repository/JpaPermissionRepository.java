package com.oauth2.app.oauth2_authorization_server.interfaces.repository;

import com.oauth2.app.oauth2_authorization_server.models.entity.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaPermissionRepository extends JpaRepository<Permissions, Long> {
    Optional<Permissions> findByActiveTrueAndName(String name);
    List<Permissions> findAllByActiveTrue();
    boolean existsByActiveTrueAndPublicIdIn(List<UUID> publicIds);
    List<Permissions> findAllByActiveTrueAndPublicIdIn(List<UUID> publicIds);
}

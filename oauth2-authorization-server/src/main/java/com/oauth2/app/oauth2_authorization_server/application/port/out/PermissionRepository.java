package com.oauth2.app.oauth2_authorization_server.application.port.out;

import com.oauth2.app.oauth2_authorization_server.domain.model.Permission;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Retained UUID for lists passed from services, but PublicIdVO for single lookups

public interface PermissionRepository {
    Optional<Permission> findByActiveTrueAndName(String name);
    List<Permission> findAllByActiveTrue();
    boolean existsByActiveTrueAndPublicIdIn(List<UUID> publicIds); // Service might pass List<UUID>
    List<Permission> findAllByActiveTrueAndPublicIdIn(List<UUID> publicIds); // Service might pass List<UUID>
    Optional<Permission> findByActiveTrueAndPublicId(PublicIdVO publicId);
    // Permission save(Permission permission); // CRUD operations usually go through application services
}

package com.oauth2.app.oauth2_authorization_server.application.port.out;

import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID; // Kept for lists or direct UUID comparisons if necessary

public interface RoleRepository {
    Optional<Role> findByNameAndActiveTrue(String name); // More specific
    boolean existsByNameAndActiveTrue(String name);
    Optional<Role> findByPublicIdAndActiveTrue(PublicIdVO publicId);
    Page<Role> findAllByActiveTrue(Pageable pageable);
    boolean existsByNameAndActiveTrueAndIdNot(String name, Integer id);
    boolean existsByPublicIdAndActiveTrue(PublicIdVO publicId);
    Role save(Role role);
    Optional<Role> findById(Integer id); // For internal checks like existsByNameAndActiveTrueAndIdNot

}

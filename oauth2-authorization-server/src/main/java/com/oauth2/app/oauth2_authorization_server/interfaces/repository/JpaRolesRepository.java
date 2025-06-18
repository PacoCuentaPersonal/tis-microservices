package com.oauth2.app.oauth2_authorization_server.interfaces.repository;

import com.oauth2.app.oauth2_authorization_server.models.entity.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRolesRepository extends JpaRepository<Roles,Long> {

    Optional<Roles> findByName(@Param("name")String name);
    boolean existsByNameAndActiveTrue(@Param("name") String name);
    Optional<Roles> findByPublicIdAndActiveTrue(@Param("publicId") UUID publicId);
    Page<Roles> findAllByActiveTrue(Pageable pageable);
    boolean existsByNameAndActiveTrueAndIdNot(String name, Integer id);
    boolean existsByPublicIdAndActiveTrue(UUID publicId);
}

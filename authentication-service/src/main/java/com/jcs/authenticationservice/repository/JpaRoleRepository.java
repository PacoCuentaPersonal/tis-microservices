package com.jcs.authenticationservice.repository;

import com.jcs.authenticationservice.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaRoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(String name);
    @Query(
            """
            SELECT r FROM RoleEntity r
            JOIN UserRoleEntity ur ON ur.role.id = r.id
            JOIN ur.user u
            WHERE u.id = :id
            AND r.active = true
            AND u.active = true
            AND ur.active = true
            """
    )
    List<RoleEntity> findAllByIdOfUser(Long id);




    Optional<RoleEntity> findByPublicIdAndActiveTrue (UUID publicId);



}

package com.jcs.authenticationservice.repository;

import com.jcs.authenticationservice.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaUserRoleRepository extends JpaRepository<UserRoleEntity, Integer> {
    @Query(
            """
            SELECT COUNT(ur) > 0 FROM UserRoleEntity ur
            JOIN ur.role r
            WHERE r.name = 'ROOT'
            AND r.active = true
            AND ur.active = true
            """
    )
    boolean isRootRoleAssigned();
}

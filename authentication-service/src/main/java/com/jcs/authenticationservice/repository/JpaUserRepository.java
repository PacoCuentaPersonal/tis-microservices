package com.jcs.authenticationservice.repository;

import com.jcs.authenticationservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity,Long> {

    @Query(
            """
            SELECT u FROM UserEntity u
            LEFT JOIN u.emailAuth ea
            WHERE ea.email = :email
            AND ea.active = true
            AND u.active = true
            """
    )
    Optional<UserEntity> findByEmail(String email);

    @Query(
            """
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
            FROM UserEntity u
            LEFT JOIN  u.emailAuth ea
            LEFT JOIN  u.oauthAuth oa
            WHERE (ea.email = :email OR oa.email = :email)
            AND ea.active = true
            AND oa.active = true
            AND u.active = true
            """
    )
    boolean existsUserByEmail (@Param(value = "email") String email);



}

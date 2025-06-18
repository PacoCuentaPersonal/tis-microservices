package com.oauth2.app.oauth2_authorization_server.interfaces.repository;

import com.oauth2.app.oauth2_authorization_server.models.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(@Param("email")String email);

    @Query(value = "SELECT * FROM t_account WHERE roles_id=:roleId",nativeQuery = true)
    Optional<Account> getAccountByRoleId(@Param("roleId")Long roleId);

    boolean existsByPublicIdAndActiveTrue(UUID publicId);

    Optional<Account> findByPublicIdAndActiveTrue(UUID publicId);

    Page<Account> findByEmailContainingIgnoreCaseAndActiveTrue(String email, Pageable pageable);

    Page<Account> findAllByActiveTrue(Pageable pageable);

}

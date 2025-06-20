package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository;

import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaAccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByEmail(@Param("email") String email);

    // La query original usaba t_account y roles_id. Ajustado a la tabla y columna esperada.
    // @Query(value = "SELECT * FROM oauth2.account WHERE role_id=:roleId", nativeQuery = true)
    // Optional<AccountEntity> getAccountByRoleId(@Param("roleId") Integer roleId);
    // Esta query específica puede requerir una interfaz de puerto de salida más específica si se usa.

    boolean existsByPublicIdAndActiveTrue(UUID publicId);

    Optional<AccountEntity> findByPublicIdAndActiveTrue(UUID publicId);

    Page<AccountEntity> findByEmailContainingIgnoreCaseAndActiveTrue(String email, Pageable pageable);

    Page<AccountEntity> findAllByActiveTrue(Pageable pageable);
}

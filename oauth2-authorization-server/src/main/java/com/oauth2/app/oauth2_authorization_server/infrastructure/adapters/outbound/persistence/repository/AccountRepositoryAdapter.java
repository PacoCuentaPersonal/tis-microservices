package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository;

import com.oauth2.app.oauth2_authorization_server.application.port.out.AccountRepository;
import com.oauth2.app.oauth2_authorization_server.domain.model.Account;
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.AccountEntity;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.RolesEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID; // Asegurarse que UUID solo se use internamente si es necesario para el mapeo o la interfaz Spring Data

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final SpringDataJpaAccountRepository springDataJpaRepository;

    @Override
    public Optional<Account> findByEmail(String email) {
        return springDataJpaRepository.findByEmail(email).map(this::mapEntityToDomain);
    }

    @Override
    public boolean existsByPublicIdAndActiveTrue(PublicIdVO publicId) {
        return springDataJpaRepository.existsByPublicIdAndActiveTrue(publicId.value());
    }

    @Override
    public Optional<Account> findByPublicIdAndActiveTrue(PublicIdVO publicId) {
        return springDataJpaRepository.findByPublicIdAndActiveTrue(publicId.value()).map(this::mapEntityToDomain);
    }

    @Override
    public Page<Account> findByEmailContainingIgnoreCaseAndActiveTrue(String email, Pageable pageable) {
        return springDataJpaRepository.findByEmailContainingIgnoreCaseAndActiveTrue(email, pageable).map(this::mapEntityToDomain);
    }

    @Override
    public Page<Account> findAllByActiveTrue(Pageable pageable) {
        return springDataJpaRepository.findAllByActiveTrue(pageable).map(this::mapEntityToDomain);
    }

    @Override
    public Account save(Account account) {
        AccountEntity accountEntity = mapDomainToEntity(account);
        AccountEntity savedEntity = springDataJpaRepository.save(accountEntity);
        return mapEntityToDomain(savedEntity);
    }

    private Account mapEntityToDomain(AccountEntity entity) {
        if (entity == null) return null;
        return Account.builder()
                .id(entity.getId())
                .publicId(new PublicIdVO(entity.getPublicId()))
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .emailVerified(entity.isEmailVerified())
                .profilePictureUrl(entity.getProfilePictureUrl())
                .employeePublicId(entity.getEmployeePublicId() != null ? new PublicIdVO(entity.getEmployeePublicId()) : null)
                .lastLoginAt(entity.getLastLoginAt())
                .active(entity.isActive())
                .role(mapRolesEntityToDomain(entity.getRole()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private AccountEntity mapDomainToEntity(Account domain) {
        if (domain == null) return null;
        AccountEntity entity = new AccountEntity();
        entity.setId(domain.getId()); 
        if (domain.getPublicId() != null) { 
            entity.setPublicId(domain.getPublicId().value());
        } // Si es nuevo y el publicId no está seteado, @UuidGenerator lo hará en la entidad.
        entity.setEmail(domain.getEmail());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setEmailVerified(domain.isEmailVerified());
        entity.setProfilePictureUrl(domain.getProfilePictureUrl());
        if (domain.getEmployeePublicId() != null) {
            entity.setEmployeePublicId(domain.getEmployeePublicId().value());
        }
        entity.setLastLoginAt(domain.getLastLoginAt());
        entity.setActive(domain.isActive());
        entity.setRole(mapRoleDomainToEntity(domain.getRole()));
        entity.setCreatedAt(domain.getCreatedAt()); // Manejado por @CreatedDate
        entity.setUpdatedAt(domain.getUpdatedAt()); // Manejado por @LastModifiedDate
        return entity;
    }

    private Role mapRolesEntityToDomain(RolesEntity entity) {
        if (entity == null) return null;
        return Role.builder()
                .id(entity.getId())
                .publicId(new PublicIdVO(entity.getPublicId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private RolesEntity mapRoleDomainToEntity(Role domain) {
        if (domain == null) return null;
        RolesEntity entity = new RolesEntity();
        // Si el rol se obtiene de la BD (ej. a través de RoleService), ya tendría su ID y PublicID.
        // Si el rol en el dominio es nuevo y se va a persistir, esta lógica podría ser diferente.
        // Asumimos que el Role en el dominio ya tiene un ID si se va a asociar.
        entity.setId(domain.getId()); 
        if (domain.getPublicId() != null) {
            entity.setPublicId(domain.getPublicId().value());
        }
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}

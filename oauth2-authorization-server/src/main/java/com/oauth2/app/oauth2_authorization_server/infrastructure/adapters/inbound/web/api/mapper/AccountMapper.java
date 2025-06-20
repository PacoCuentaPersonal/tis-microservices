package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.mapper;

import com.oauth2.app.oauth2_authorization_server.domain.model.Account;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.AccountResponse; // Path corregido
import org.springframework.stereotype.Component;

@Component // Los mappers pueden ser componentes de Spring para facilitar su inyección
public class AccountMapper {
    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }
        return AccountResponse.builder()
                .publicId(account.getPublicId() != null ? account.getPublicId().value() : null)
                .username(account.getUsername())
                .email(account.getEmail())
                .profilePictureUrl(account.getProfilePictureUrl())
                .role(account.getRole() != null ? account.getRole().getName() : null)
                .emailVerified(account.isEmailVerified())
                .lastLoginAt(account.getLastLoginAt())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}

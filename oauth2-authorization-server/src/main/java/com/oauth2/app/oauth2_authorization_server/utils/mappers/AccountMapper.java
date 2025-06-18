package com.oauth2.app.oauth2_authorization_server.utils.mappers;

import com.oauth2.app.oauth2_authorization_server.dto.response.AccountResponse;
import com.oauth2.app.oauth2_authorization_server.models.entity.Account;

public class AccountMapper {
    public static AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }
        return AccountResponse.builder()
                .publicId(account.getPublicId())
                .username(account.getUsername())
                .email(account.getEmail())
                .profilePictureUrl(account.getProfilePictureUrl())
                .role(account.getRoles().getName())
                .emailVerified(account.isEmailVerified())
                .lastLoginAt(account.getLastLoginAt())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}

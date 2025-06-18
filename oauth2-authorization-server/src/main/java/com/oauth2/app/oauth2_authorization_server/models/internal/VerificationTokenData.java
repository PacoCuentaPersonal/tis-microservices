package com.oauth2.app.oauth2_authorization_server.models.internal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VerificationTokenData(
        @NotNull(message = "Account ID cannot be null")
        UUID accountId,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email

) {

    public static VerificationTokenData forEmailVerification(UUID accountId, String email) {
        return new VerificationTokenData(accountId, email);
    }

}
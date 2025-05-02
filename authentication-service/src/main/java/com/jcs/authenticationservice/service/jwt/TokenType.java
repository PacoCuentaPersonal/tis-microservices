package com.jcs.authenticationservice.service.jwt;

/**
 * Enum representing different types of JWT tokens.
 */
public enum TokenType {
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    RESET_PASSWORD_TOKEN,
    EMAIL_VERIFICATION_TOKEN
}
package com.oauth2.app.oauth2_authorization_server.infrastructure.exception.handler;

import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.infrastructure.exception.throwers.token.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class TokenExceptionHandler {

    @ExceptionHandler(TokenEncryptionException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleTokenEncryption(TokenEncryptionException e) {
        log.error("Token encryption error: {}", e.getMessage(), e);

        EnvelopeResponse<Void> response = EnvelopeResponse.error(
                "Failed to encrypt data",
                List.of(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(TokenDecryptionException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleTokenDecryption(TokenDecryptionException e) {
        log.error("Token decryption error: {}", e.getMessage(), e);

        EnvelopeResponse<Void> response = EnvelopeResponse.error(
                "Failed to decrypt data",
                List.of(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleInvalidToken(InvalidTokenException e) {
        log.warn("Invalid token: {}", e.getMessage());

        EnvelopeResponse<Void> response = EnvelopeResponse.error(
                "Invalid token provided",
                List.of(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleExpiredToken(TokenExpiredException e) {
        log.warn("Expired token: {}", e.getMessage());

        EnvelopeResponse<Void> response = EnvelopeResponse.error(
                "Token has expired",
                List.of(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(TokenKeyNotFoundException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleKeyNotFound(TokenKeyNotFoundException e) {
        log.warn("Token key not found: {}", e.getMissingKey());

        EnvelopeResponse<Void> response = EnvelopeResponse.error(
                "Required data not found in token",
                List.of(e.getMessage(), "Missing key: " + e.getMissingKey())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleJwtException(JwtException e) {
        log.warn("JWT error: {}", e.getMessage());

        EnvelopeResponse<Void> response = EnvelopeResponse.error(
                "Invalid JWT token format",
                List.of("Token is malformed or has invalid signature")
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());

        EnvelopeResponse<Void> response = EnvelopeResponse.error(
                "Invalid request parameters",
                List.of(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
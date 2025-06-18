package com.oauth2.app.oauth2_authorization_server.service.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth2.app.oauth2_authorization_server.exception.throwers.token.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DataTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final ObjectMapper objectMapper;
    private final DataTokenService dataTokenService;

    public DataTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, ObjectMapper objectMapper, DataTokenService dataTokenService) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.objectMapper = objectMapper;
        this.dataTokenService = dataTokenService;
    }

    /**
     * Encripta datos con duración personalizada - Versión genérica
     */
    public <T> String encryptData(String key, T value, Duration duration) {
        if (key == null || key.trim().isEmpty()) {
            throw new TokenEncryptionException("Key cannot be null or empty");
        }

        if (value == null) {
            throw new TokenEncryptionException("Value cannot be null");
        }

        try {
            Instant now = Instant.now();

            // Usar un subject diferente para distinguir de tokens OAuth2
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("data-service") // Diferente del Authorization Server
                    .subject("encrypted-data") // Diferente de "user" tokens
                    .issuedAt(now)
                    .expiresAt(now.plus(duration))
                    .claim("purpose", "data-encryption")
                    .claim("type", "data-token")
                    .claim(key, value)
                    .build();

            String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            log.debug("Data encrypted successfully for key: {} of type: {}", key, value.getClass().getSimpleName());
            return token;

        } catch (Exception e) {
            log.error("Failed to encrypt data for key: {}", key, e);
            throw new TokenEncryptionException("Failed to encrypt data for key: " + key, e);
        }
    }
    /**
     * Desencripta y obtiene el valor por clave
     */
    public <T> T decryptData(String token, String key, Class<T> type) {
        if (key == null || key.trim().isEmpty()) {
            throw new TokenDecryptionException("Key cannot be null or empty");
        }

        if (type == null) {
            throw new TokenDecryptionException("Type cannot be null");
        }

        try {
            Jwt jwt = validateAndDecodeToken(token);
            Object claim = jwt.getClaim(key);

            if (claim == null) {
                throw new TokenKeyNotFoundException(key);
            }

            return convertToType(claim, type);

        } catch (TokenException e) {
            throw e; // Re-lanzar excepciones de token
        } catch (Exception e) {
            log.error("Failed to decrypt data for key: {}", key, e);
            throw new TokenDecryptionException("Failed to decrypt data for key: " + key, e);
        }
    }

    /**
     * Obtiene todos los claims de datos del token
     */
    public Map<String, Object> decryptAllData(String token) {
        try {
            Jwt jwt = validateAndDecodeToken(token);
            Map<String, Object> claims = new HashMap<>(jwt.getClaims());
            return claims;

        } catch (TokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to decrypt all data from token", e);
            throw new TokenDecryptionException("Failed to decrypt all data from token", e);
        }
    }

    /**
     * Valida si el token es válido (no expirado, firma correcta)
     */
    public boolean isValidToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            validateAndDecodeToken(token);
            return true;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }



    // --- MÉTODOS PRIVADOS ---

    private Jwt validateAndDecodeToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new InvalidTokenException("Token cannot be null or empty");
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);

            // Verificar que sea un token de datos (NO un token OAuth2)
            if (!"data-token".equals(jwt.getClaim("type"))) {
                throw new InvalidTokenException("Invalid token type - not a data token");
            }

            if (!"data-encryption".equals(jwt.getClaim("purpose"))) {
                throw new InvalidTokenException("Invalid token purpose");
            }

            // Verificar expiración
            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(Instant.now())) {
                throw new TokenExpiredException("Token has expired");
            }

            return jwt;

        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid token format or signature", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T convertToType(Object claim, Class<T> type) {
        try {
            if (type.isInstance(claim)) {
                return type.cast(claim);
            }

            // Para tipos primitivos y wrappers
            if (type == String.class) {
                return type.cast(claim.toString());
            }

            if (type == Integer.class && claim instanceof Number) {
                return type.cast(((Number) claim).intValue());
            }

            if (type == Long.class && claim instanceof Number) {
                return type.cast(((Number) claim).longValue());
            }

            if (type == Boolean.class) {
                return type.cast(Boolean.valueOf(claim.toString()));
            }

            // Para objetos complejos, usar ObjectMapper
            return objectMapper.convertValue(claim, type);

        } catch (Exception e) {
            throw new TokenDecryptionException("Cannot convert claim to type: " + type.getSimpleName(), e);
        }
    }
}
package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth2.app.oauth2_authorization_server.application.port.out.IVerificationTokenService; // Added import
import com.oauth2.app.oauth2_authorization_server.infrastructure.exception.throwers.token.*; // Updated import
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DataTokenService implements IVerificationTokenService { // Implemented interface

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, ObjectMapper objectMapper) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.objectMapper = objectMapper;
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

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("data-service")
                    .subject("encrypted-data")
                    .issuedAt(now)
                    .expiresAt(now.plus(duration))
                    .claim("purpose", "data-encryption")
                    .claim("type", "data-token")
                    .claim(key, value) // The map 'data' will be a single claim named 'keyName'
                    .build();

            String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            log.debug("Data encrypted successfully for key: {} of type: {}", key, value.getClass().getSimpleName());
            return token;

        } catch (Exception e) {
            log.error("Failed to encrypt data for key: {}", key, e);
            throw new TokenEncryptionException("Failed to encrypt data for key: " + key, e);
        }
    }

    // Implementation for IVerificationTokenService
    @Override
    public String encryptData(String keyName, Map<String, Object> data, Duration validity) {
        // Calls the generic method, storing the map as a single claim.
        return this.encryptData(keyName, (Object) data, validity);
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
            throw e; 
        } catch (Exception e) {
            log.error("Failed to decrypt data for key: {}", key, e);
            throw new TokenDecryptionException("Failed to decrypt data for key: " + key, e);
        }
    }

    /**
     * Obtiene todos los claims de datos del token
     */
    @Override // Added @Override
    public Map<String, Object> decryptAllData(String token) {
        try {
            Jwt jwt = validateAndDecodeToken(token);
            Map<String, Object> claims = new HashMap<>(jwt.getClaims());
            // Remove standard JWT claims if only custom data is desired, or keep them if useful.
            // For example: claims.remove("iss"); claims.remove("sub"); ...
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

    private Jwt validateAndDecodeToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new InvalidTokenException("Token cannot be null or empty");
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);

            if (!"data-token".equals(jwt.getClaim("type"))) {
                throw new InvalidTokenException("Invalid token type - not a data token");
            }

            if (!"data-encryption".equals(jwt.getClaim("purpose"))) {
                throw new InvalidTokenException("Invalid token purpose");
            }

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
            // Use ObjectMapper for more complex conversions, especially if 'claim' is a Map
            return objectMapper.convertValue(claim, type);
        } catch (Exception e) {
            throw new TokenDecryptionException("Cannot convert claim to type: " + type.getSimpleName(), e);
        }
    }
}

package com.jcs.authenticationservice.orchestrator;

import com.jcs.authenticationservice.config.cookie.JwtConfig;
import com.jcs.authenticationservice.service.jwt.TokenType;
import com.jcs.authenticationservice.usecase.auth.ICookieService;
import com.jcs.authenticationservice.usecase.auth.ITokenService;
import com.jcs.authenticationservice.usecase.auth.ITokenStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthOrchestrator {
    private final ITokenService tokenService;
    private final ICookieService cookieService;
    private final JwtConfig jwtConfig;
    private final ITokenStorageService tokenStorageService;

    public record TokenWithCookie(String token, ResponseCookie cookie) {
        public boolean hasCookie() {
            return cookie != null;
        }
    }
    /**
     * Genera un token sin cookie
     */
    public String generateTokenOnly(String username, TokenType tokenType, Map<String, Object> claims) {
        return tokenService.generateToken(username, tokenType, claims);
    }

    /**
     * Genera un token con cookie
     */
    public TokenWithCookie generateTokenWithCookie(String username, TokenType tokenType, Map<String, Object> claims) {
        String token = tokenService.generateToken(username, tokenType, claims);
        System.out.println("Token size: " + token.getBytes(StandardCharsets.UTF_8).length + " bytes");
        ResponseCookie cookie = createTokenCookie(token, tokenType);
        System.out.println("Cookie generada: " + cookie.toString());
        return new TokenWithCookie(token, cookie);
    }

    /**
     * Realiza el logout de un usuario invalidando sus tokens
     */
    public TokenWithCookie invalidCookieOfToken(String token, TokenType tokenType) {
        if (token == null || token.isEmpty()) {
            String cookieName = jwtConfig.getTokens().get(tokenType).getCookieName();
            ResponseCookie expiredCookie = cookieName != null ? cookieService.createExpiredCookie(cookieName) : null;
            return new TokenWithCookie(null, expiredCookie);
        }

        // Calculamos el tiempo restante del token para la blacklist
        long ttlSeconds = tokenService.calculateRemainingTtl(token);
        if (ttlSeconds > 0) {
            tokenStorageService.blacklistToken(token, ttlSeconds, TimeUnit.SECONDS);
        }

        // Creamos una cookie expirada para eliminar la cookie del cliente
        String cookieName = jwtConfig.getTokens().get(tokenType).getCookieName();
        ResponseCookie expiredCookie = cookieName != null ? cookieService.createExpiredCookie(cookieName) : null;

        return new TokenWithCookie(null, expiredCookie);
    }

    /**
     * Valida un token
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return tokenService.validateToken(token);
    }

    /**
     * Crea una cookie para un token
     */
    private ResponseCookie createTokenCookie(String token, TokenType tokenType) {
        String cookieName = jwtConfig.getTokens().get(tokenType).getCookieName();
        if (cookieName == null) {
            return null;
        }

        Duration expiration = Duration.ofMillis(
                jwtConfig.getTokens().get(tokenType).getExpirationMs()
        );

        return cookieService.createCookie(cookieName, token, expiration)
                .orElse(null);
    }

    /**
     * Obtiene el nombre de la cookie para los tokens de acceso
     */
    public String getAccessTokenCookieName() {
        return jwtConfig.getTokens().get(TokenType.ACCESS_TOKEN).getCookieName();
    }

    /**
     * Obtiene el nombre de la cookie para los tokens de refresco
     */
    public String getRefreshTokenCookieName() {
        return jwtConfig.getTokens().get(TokenType.REFRESH_TOKEN).getCookieName();
    }
}
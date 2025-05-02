package com.jcs.authenticationservice.service.jwt;

import com.jcs.authenticationservice.config.cookie.JwtConfig;
import com.jcs.authenticationservice.usecase.auth.ITokenService;
import com.jcs.authenticationservice.usecase.auth.ITokenStorageService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService implements ITokenService {
    private static final String TYPE_CLAIM = "type";
    private final KeyGeneratorService keyGeneratorService;
    private final JwtConfig jwtConfig;
    private final ITokenStorageService tokenStorageService;

    public String generateToken(String username, TokenType tokenType, Map<String, Object> claims) {

        long expirationMs = jwtConfig.getTokens().get(tokenType).getExpirationMs();
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMs);
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim(TYPE_CLAIM, tokenType.name())
                .claims(claims)
                .signWith(keyGeneratorService.generatePrivateKey())
                .compact();
    }
    public Claims extractClaims (String token){
        return Jwts.parser()
                .verifyWith(keyGeneratorService.generatePublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public Date extractExpiration(String token){
        Claims claims = extractClaims(token);
        return claims.getExpiration();
    }
    public String extractSubject(String token){
        return extractClaims(token).getSubject();
    }
    public String extractTokenType(String token){
        return (String) extractClaims(token).get("type");
    }
    public boolean validateToken(String jwtToken) {
        try {
            boolean isNotExpired = extractExpiration(jwtToken).after(new Date());
            boolean isNotBlacklisted = !tokenStorageService.isTokenBlacklisted(jwtToken);
            return isNotExpired && isNotBlacklisted;
        } catch (Exception e) {
            return false;
        }
    }
    public long calculateRemainingTtl(String token) {
        try {
            Date expiration = extractExpiration(token);
            long now = System.currentTimeMillis();
            long expirationTime = expiration.getTime();
            return Math.max(0, (expirationTime - now) / 1000);
        } catch (Exception e) {
            return 0;
        }
    }


}

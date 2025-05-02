package com.jcs.authenticationservice.usecase.auth;

import com.jcs.authenticationservice.service.jwt.TokenType;

import java.util.Date;
import java.util.Map;

public interface ITokenService {
    String generateToken(String subject, TokenType tokenType, Map<String, Object> claims);
    boolean validateToken(String token);
    String extractSubject(String token);
    Date extractExpiration(String token);
    String extractTokenType(String token);
    long calculateRemainingTtl(String token);

}

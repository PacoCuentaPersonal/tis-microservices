package com.jcs.authenticationservice.config.cookie;

import com.jcs.authenticationservice.service.jwt.TokenType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter @Setter
public class JwtConfig {
    private Map<TokenType, TokenConfig> tokens = new EnumMap<>(TokenType.class);

    @Getter @Setter
    public static class TokenConfig {
        private long expirationMs;
        private String cookieName;
    }
}
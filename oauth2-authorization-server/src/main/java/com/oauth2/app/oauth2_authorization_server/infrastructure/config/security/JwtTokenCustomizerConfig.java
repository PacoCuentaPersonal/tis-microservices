package com.oauth2.app.oauth2_authorization_server.infrastructure.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenCustomizerConfig implements OAuth2TokenCustomizer<JwtEncodingContext> {

    public void customize(JwtEncodingContext context) {
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            Authentication principal = context.getPrincipal();

            Set<String> authorities = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            Set<String> roles = authorities.stream()
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .collect(Collectors.toSet());

            Set<String> permissions = authorities.stream()
                    .filter(authority -> authority.startsWith("PERMISSION_"))
                    .collect(Collectors.toSet());

            context.getClaims().claim("roles", roles);
            context.getClaims().claim("permissions", permissions);

        }
    }
}
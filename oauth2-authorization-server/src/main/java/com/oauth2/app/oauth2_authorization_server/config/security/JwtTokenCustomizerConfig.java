package com.oauth2.app.oauth2_authorization_server.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenCustomizerConfig implements OAuth2TokenCustomizer<JwtEncodingContext> {

    public void customize(JwtEncodingContext context) {
        // Solo personalizamos el token de acceso
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            Authentication principal = context.getPrincipal();

            // Extraemos todas las autoridades (roles y permisos)
            Set<String> authorities = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            // Separamos roles de permisos (asumiendo la convención ROLE_ y PERMISSION_)
            Set<String> roles = authorities.stream()
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .collect(Collectors.toSet());

            Set<String> permissions = authorities.stream()
                    .filter(authority -> authority.startsWith("PERMISSION_"))
                    .collect(Collectors.toSet());

            // Agregamos claims al token JWT
            context.getClaims().claim("roles", roles);
            context.getClaims().claim("permissions", permissions);

            // También puedes agregar información adicional del usuario si la necesitas
            // Por ejemplo, si has extendido UserDetails para incluir el tenant_id:
            // context.getClaims().claim("tenant_id", ((CustomUserDetails)principal).getTenantId());
        }
    }
}
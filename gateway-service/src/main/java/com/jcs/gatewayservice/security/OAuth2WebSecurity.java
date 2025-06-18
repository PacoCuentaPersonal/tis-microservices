package com.jcs.gatewayservice.security;

import com.jcs.gatewayservice.config.CorsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
@EnableWebFluxSecurity
public class OAuth2WebSecurity {
    @Value("${auth0.audience}")
    private String audience;

    private final CorsConfig corsConfig;
    public OAuth2WebSecurity(CorsConfig corsConfig) {
        this.corsConfig = corsConfig;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(corsSpec -> corsSpec.configurationSource(corsConfig.corsConfigurationSource()))
                .authorizeExchange(
                        authorizeExchangeSpec ->
                                authorizeExchangeSpec
                                        .anyExchange().authenticated()
                ).oauth2ResourceServer(
                        oAuth2->oAuth2.jwt(
                                jwtConfigurer-> jwtConfigurer
                                        .jwtAuthenticationConverter(converter())));
        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter converter(){
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt-> {
            JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
            Set<GrantedAuthority> authorities = new HashSet<>(defaultConverter.convert(jwt));

            Optional.ofNullable(jwt.getClaimAsStringList("user_roles"))
                    .stream()
                    .flatMap(Collection::stream)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .forEach(authorities::add);

            Optional.ofNullable(jwt.getClaimAsStringList("permissions"))
                    .stream()
                    .flatMap(Collection::stream)
                    .map(permission -> new SimpleGrantedAuthority(permission))
                    .forEach(authorities::add);

            return authorities;
        });
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}

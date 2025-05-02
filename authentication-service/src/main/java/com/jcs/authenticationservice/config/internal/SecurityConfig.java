package com.jcs.authenticationservice.config.internal;

import com.jcs.authenticationservice.config.cors.CorsConfig;
import com.jcs.authenticationservice.filter.JwtCookieAuthFilter;
import com.jcs.authenticationservice.service.auth.OidcUserServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final OidcUserServiceImpl oidcUserServiceImpl;
    private final SecurityUnits securityUnits;
    private final JwtCookieAuthFilter jwtCookieAuthFilter;

    public SecurityConfig(OidcUserServiceImpl oidcUserServiceImpl, SecurityUnits securityUnits, JwtCookieAuthFilter jwtCookieAuthFilter) {
        this.oidcUserServiceImpl = oidcUserServiceImpl;
        this.securityUnits = securityUnits;
        this.jwtCookieAuthFilter = jwtCookieAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .sessionFixation().none()
            )
            .authorizeHttpRequests(
                auth -> auth
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .anyRequest().authenticated()
            )
            .securityContext(securityContext ->securityContext
                    .requireExplicitSave(false)
            )
            .oauth2Login(oauth -> oauth
                    .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserServiceImpl)
                    )
            )
            .authenticationProvider(securityUnits.authenticationProvider())
            .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();

    }
}

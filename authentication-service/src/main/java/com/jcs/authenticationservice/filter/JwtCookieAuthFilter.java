package com.jcs.authenticationservice.filter;

import com.jcs.authenticationservice.config.cookie.JwtConfig;
import com.jcs.authenticationservice.orchestrator.AuthOrchestrator;
import com.jcs.authenticationservice.service.auth.UserDetailsServiceImpl;
import com.jcs.authenticationservice.service.jwt.TokenType;
import com.jcs.authenticationservice.usecase.auth.ITokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class JwtCookieAuthFilter extends OncePerRequestFilter {
    private final ITokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthOrchestrator authOrchestrator;
    private final JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Intentar autenticar con access token
        Optional<String> accessToken = extractTokenFromCookies(request, TokenType.ACCESS_TOKEN);
        if (accessToken.isPresent() && tokenService.validateToken(accessToken.get())) {
            handleAccessToken(request, response, filterChain, accessToken.get());
            return;
        }

        // 2. Si no hay access token válido, intentar con refresh token
        Optional<String> refreshToken = extractTokenFromCookies(request, TokenType.REFRESH_TOKEN);
        if (refreshToken.isPresent() && tokenService.validateToken(refreshToken.get())) {
            handleRefreshToken(request, response, refreshToken.get());
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractTokenFromCookies(HttpServletRequest request, TokenType tokenType) {
        if (request.getCookies() == null) return Optional.empty();

        String cookieName = jwtConfig.getTokens().get(tokenType).getCookieName();
        if (cookieName == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void handleAccessToken(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain,
                                   String token) throws IOException, ServletException {

        String username = tokenService.extractSubject(token);
        authenticateUser(username, request);
        filterChain.doFilter(request, response);
    }

    private void handleRefreshToken(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String refreshToken) {

        String username = tokenService.extractSubject(refreshToken);
        Map<String, Object> claims = Map.of(
                "roles", userDetailsService.loadUserByUsername(username).getAuthorities()
        );

        // Generar nuevos tokens y cookies
        AuthOrchestrator.TokenWithCookie accessTokens = authOrchestrator
                .generateTokenWithCookie(username, TokenType.ACCESS_TOKEN, claims);
        if (accessTokens.hasCookie()) {
            response.addHeader("Set-Cookie", accessTokens.cookie().toString());
        }

        // Generar nuevo REFRESH_TOKEN
        AuthOrchestrator.TokenWithCookie refreshTokens = authOrchestrator
                .generateTokenWithCookie(username, TokenType.REFRESH_TOKEN, claims);
        if (refreshTokens.hasCookie()) {
            response.addHeader("Set-Cookie", refreshTokens.cookie().toString());
        }

        authenticateUser(username, request);
        addTokenInfoHeaders(response, username);
    }

    private void authenticateUser(String username, HttpServletRequest request) {
        var user = userDetailsService.loadUserByUsername(username);
        var authToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void addTokenInfoHeaders(HttpServletResponse response, String username) {
        response.setHeader("X-Token-Expires-In",
                String.valueOf(jwtConfig.getTokens().get(TokenType.ACCESS_TOKEN).getExpirationMs() / 1000));
        response.setHeader("X-Authenticated-User", username);
    }
}
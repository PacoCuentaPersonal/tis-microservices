package com.jcs.authenticationservice.service.user;

import com.jcs.authenticationservice.config.internal.CustomUser;
import com.jcs.authenticationservice.dto.permission.response.ModuleResponse;
import com.jcs.authenticationservice.dto.user.request.LoginDto;
import com.jcs.authenticationservice.dto.user.response.ResponseAuthenticationDto;
import com.jcs.authenticationservice.orchestrator.AuthOrchestrator;
import com.jcs.authenticationservice.service.jwt.TokenType;
import com.jcs.authenticationservice.service.permission.ModuleServiceImpl;
import com.jcs.authenticationservice.usecase.auth.IAuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final ModuleServiceImpl moduleService;
    private final AuthOrchestrator authOrchestrator;

    @Override
    public Pair<ResponseAuthenticationDto, HttpHeaders> login(LoginDto dto, HttpServletRequest request) {
        // Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        // Obtener detalles del usuario autenticado
        CustomUser user = (CustomUser) authentication.getPrincipal();
        String username = user.getUsername();

        // Obtener módulos para el usuario según su rol
        List<ModuleResponse> moduleToNavigate = moduleService.getAllModuleWithRoleId(user.getRolesId());

        // Verificar si el usuario ya tiene tokens válidos
        HttpHeaders headers = new HttpHeaders();
        boolean hasValidTokens = checkForValidTokens(request);

        if (!hasValidTokens) {
            // Solo generar nuevos tokens si no tiene tokens válidos
            AuthOrchestrator.TokenWithCookie accessToken = authOrchestrator.generateTokenWithCookie(username, TokenType.ACCESS_TOKEN, null);
            AuthOrchestrator.TokenWithCookie refreshToken = authOrchestrator.generateTokenWithCookie(username, TokenType.REFRESH_TOKEN, null);

            if (accessToken.cookie() != null) {
                headers.add(HttpHeaders.SET_COOKIE, accessToken.cookie().toString());
            }
            if (refreshToken.cookie() != null) {
                headers.add(HttpHeaders.SET_COOKIE, refreshToken.cookie().toString());
            }
        }

        // Construir la respuesta
        ResponseAuthenticationDto build = ResponseAuthenticationDto
                .builder()
                .email(username)
                .pendingTask(List.of())
                .avatarUrl("http://localhost:8012/eiufwebfiinweiudnwe/jen/avatar.png")
                .modules(moduleToNavigate)
                .build();

        return Pair.of(build, headers);
    }

    /**
     * Verifica si existen tokens válidos en las cookies actuales
     */
    private boolean checkForValidTokens(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        // Buscar cookies de token
        Optional<Cookie> accessCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(authOrchestrator.getAccessTokenCookieName()))
                .findFirst();

        // Verificar si el token de acceso es válido
        if (accessCookie.isPresent()) {
            String token = accessCookie.get().getValue();
            if (authOrchestrator.validateToken(token)) {
                return true;
            }
        }

        // Si no hay token de acceso válido, verificamos el refresh token
        Optional<Cookie> refreshCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(authOrchestrator.getRefreshTokenCookieName()))
                .findFirst();

        if (refreshCookie.isPresent()) {
            String token = refreshCookie.get().getValue();
            return authOrchestrator.validateToken(token);
        }

        return false;
    }

    @Override
    public HttpHeaders logout(String accessToken, String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        // Procesar el access token
        if (accessToken != null) {
            boolean b = authOrchestrator.validateToken(accessToken);
            if (!b){
                throw new RuntimeException("JWT NO VALID");
            }
            AuthOrchestrator.TokenWithCookie result =
                    authOrchestrator.invalidCookieOfToken(accessToken, TokenType.ACCESS_TOKEN);
            if (result.hasCookie()) {
                headers.add(HttpHeaders.SET_COOKIE, result.cookie().toString());
            }
        }
        // Procesar el refresh token
        if (refreshToken != null) {
            AuthOrchestrator.TokenWithCookie result =
                    authOrchestrator.invalidCookieOfToken(refreshToken, TokenType.REFRESH_TOKEN);
            if (result.hasCookie()) {
                headers.add(HttpHeaders.SET_COOKIE, result.cookie().toString());
            }
        }

        return headers;
    }
}
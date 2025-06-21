package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class Oauth2UserController {

    @GetMapping("/user/me")
    public Map<String, Object> getUserInfo(Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("username", authentication.getName());
        userInfo.put("authenticated", authentication.isAuthenticated());

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Set<String> roles = authorities.stream()
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(auth -> auth.substring(5))
                .collect(Collectors.toSet());

        Set<String> permissions = authorities.stream()
                .filter(auth -> auth.startsWith("PERMISSION_"))
                .map(auth -> auth.substring(11))
                .collect(Collectors.toSet());

        userInfo.put("roles", roles);
        userInfo.put("permissions", permissions);
        userInfo.put("authorities", authorities);

        return userInfo;
    }

    @GetMapping("/user/check")
    public Map<String, Boolean> checkAuthentication(Authentication authentication) {
        Map<String, Boolean> status = new HashMap<>();
        status.put("authenticated", authentication != null && authentication.isAuthenticated());
        return status;
    }
}

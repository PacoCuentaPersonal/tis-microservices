package com.jcs.authenticationservice.service.cookie;

import com.jcs.authenticationservice.config.cookie.CookieConfig;
import com.jcs.authenticationservice.usecase.auth.ICookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class CookieServiceImpl implements ICookieService {
    private final CookieConfig cookieConfig;

    public Optional<ResponseCookie> createCookie(String name, String value, Duration maxAge) {
        if (!cookieConfig.isEnabled()) {
            return Optional.empty();
        }

        return Optional.of(buildCookie(name, value, maxAge, cookieConfig.getDefaults()));
    }

    public ResponseCookie createExpiredCookie(String name) {
        return buildCookie(name, "", Duration.ZERO, cookieConfig.getDefaults());
    }

    private ResponseCookie buildCookie(String name, String value, Duration maxAge,
                                       CookieConfig.Defaults config) {

        return ResponseCookie.from(name, value)
                .httpOnly(config.isHttpOnly())
                .secure(config.isSecure())
                .path(config.getPath())
                .sameSite(config.getSameSite().name())
                .domain(emptyToNull(config.getDomain()))
                .maxAge(maxAge.getSeconds())
                .build();
    }

    private String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }
}
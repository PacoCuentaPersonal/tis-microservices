package com.jcs.authenticationservice.usecase.auth;

import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Optional;

public interface ICookieService {
    /**
     * Crea una cookie con un valor y tiempo de vida máximo
     */
    Optional<ResponseCookie> createCookie(String name, String value, Duration maxAge);

    /**
     * Crea una cookie expirada para eliminar la cookie existente
     */
    ResponseCookie createExpiredCookie(String name);
}
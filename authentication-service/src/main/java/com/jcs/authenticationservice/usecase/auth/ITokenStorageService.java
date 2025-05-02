package com.jcs.authenticationservice.usecase.auth;

import java.util.concurrent.TimeUnit;

public interface ITokenStorageService {
    /**
     * Añade un token a una lista negra
     */
    void blacklistToken(String token, long ttl, TimeUnit unit);

    /**
     * Verifica si un token está en la lista negra
     */
    boolean isTokenBlacklisted(String token);
}
package com.jcs.authenticationservice.usecase.auth;

import com.jcs.authenticationservice.dto.user.request.LoginDto;
import com.jcs.authenticationservice.dto.user.response.ResponseAuthenticationDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;

public interface IAuthenticationService {
    /**
     * Realiza el login de un usuario
     */
    Pair<ResponseAuthenticationDto, HttpHeaders> login(LoginDto dto, HttpServletRequest request);

    /**
     * Realiza el logout de un usuario
     */
    HttpHeaders logout(String accessToken, String refreshToken);
}
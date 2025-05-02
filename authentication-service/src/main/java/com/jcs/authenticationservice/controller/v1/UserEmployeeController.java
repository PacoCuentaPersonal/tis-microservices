package com.jcs.authenticationservice.controller.v1;

import com.jcs.authenticationservice.dto.user.request.LoginDto;
import com.jcs.authenticationservice.dto.user.response.ResponseAuthenticationDto;
import com.jcs.authenticationservice.shared.response.ApiResponseBuilder;
import com.jcs.authenticationservice.shared.response.ApiResponseDto;
import com.jcs.authenticationservice.usecase.auth.IAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class UserEmployeeController {
    private final IAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<ResponseAuthenticationDto>> login(@RequestBody LoginDto dto, HttpServletRequest request) {
        Pair<ResponseAuthenticationDto, HttpHeaders> result = authenticationService.login(dto , request);
        return ApiResponseBuilder.<ResponseAuthenticationDto>builder()
                .httpStatus(HttpStatus.OK)
                .message("Autenticación exitosa")
                .data(result.getFirst())
                .headers(result.getSecond()) // Añade los headers con las cookies
                .build()
                .toResponseEntity();
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponseDto<String>> test() {
        return ApiResponseBuilder.<String>builder()
                .httpStatus(HttpStatus.OK)
                .data("Hello, endpoint free")
                .build()
                .toResponseEntity();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<Void>> logout(
            @CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {

        if (accessToken == null && refreshToken == null) {
            return ApiResponseBuilder.<Void>builder()
                    .httpStatus(HttpStatus.BAD_REQUEST) //
                    .message("No hay sesión activa para cerrar")
                    .build()
                    .toResponseEntity();
        }
        HttpHeaders headers = authenticationService.logout(accessToken, refreshToken);

        return ApiResponseBuilder.<Void>builder()
                .httpStatus(HttpStatus.OK)
                .message("Logout exitoso")
                .headers(headers) // Cookies de expiración
                .build()
                .toResponseEntity();
    }


}
package com.jcs.authenticationservice.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {
    private final ResponseStatus status;
    private final String message;
    private final T data;
    private final List<String> errors;
    private final String timestamp;
    private final Map<String, Object> metadata; // Nuevo campo para propiedades extras

    public static <T> ApiResponseDto<T> success(T data) {
        return ApiResponseDto.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .timestamp(Instant.now().toString())
                .data(data)
                .metadata(null)
                .build();
    }

    public static ApiResponseDto<Void> error(String message, List<String> errors) {
        return ApiResponseDto.<Void>builder()
                .status(ResponseStatus.ERROR)
                .message(message)
                .errors(errors)
                .timestamp(Instant.now().toString())
                .metadata(null)
                .build();
    }
}
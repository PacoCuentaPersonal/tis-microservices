package com.jcs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor  // Necesario para Jackson
@AllArgsConstructor // Necesario para el Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnvelopeResponse<T> {
    private ResponseStatus status;
    private String message;
    private T data;
    private List<String> errors;
    private String timestamp;


    public static <T> EnvelopeResponse<T> success(T data) {
        return EnvelopeResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .timestamp(Instant.now().toString())
                .data(data)
                .build();
    }

    public static EnvelopeResponse<Void> error(String message, List<String> errors) {
        return EnvelopeResponse.<Void>builder()
                .status(ResponseStatus.ERROR)
                .message(message)
                .errors(errors)
                .timestamp(Instant.now().toString())
                .build();
    }
}
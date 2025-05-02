package com.jcs.authenticationservice.shared.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

public final class ApiResponseBuilder<T> {
    private final HttpStatus httpStatus;
    private final ResponseStatus status;
    private final String message;
    private final T data;
    private final List<String> errors;
    private final String timestamp;
    private final HttpHeaders headers;
    private final Map<String, Object> extraProperties;

    private ApiResponseBuilder(Builder<T> builder) {
        this.httpStatus = builder.httpStatus;
        this.status = builder.status;
        this.message = builder.message;
        this.data = builder.data;
        this.errors = Collections.unmodifiableList(builder.errors != null ? builder.errors : List.of());
        this.timestamp = builder.timestampSupplier.get();
        this.headers = builder.headers != null ? new HttpHeaders(builder.headers) : new HttpHeaders();
        this.extraProperties = Collections.unmodifiableMap(builder.extraProperties != null ?
                new LinkedHashMap<>(builder.extraProperties) : Map.of());
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public ResponseEntity<ApiResponseDto<T>> toResponseEntity() {
        ApiResponseDto<T> responseBody = ApiResponseDto.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .errors(errors.isEmpty() ? null : errors)
                .timestamp(timestamp)
                .metadata(extraProperties.isEmpty() ? null : extraProperties)
                .build();

        return ResponseEntity.status(httpStatus)
                .headers(headers)
                .body(responseBody);
    }

    // Builder mejorado
    public static final class Builder<T> {
        private HttpStatus httpStatus = HttpStatus.OK;
        private ResponseStatus status = ResponseStatus.SUCCESS;
        private String message;
        private T data;
        private List<String> errors;
        private HttpHeaders headers;
        private Map<String, Object> extraProperties;
        private Supplier<String> timestampSupplier = () -> Instant.now().toString();

        private Builder() {}

        // Métodos básicos
        public Builder<T> httpStatus(HttpStatus httpStatus) {
            this.httpStatus = Objects.requireNonNull(httpStatus);
            return this;
        }

        public Builder<T> status(ResponseStatus status) {
            this.status = Objects.requireNonNull(status);
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        // Métodos para colecciones (evitan crear nuevas instancias)
        public Builder<T> errors(List<String> errors) {
            this.errors = errors != null ? new ArrayList<>(errors) : null;
            return this;
        }

        public Builder<T> addError(String error) {
            if (this.errors == null) {
                this.errors = new ArrayList<>();
            }
            this.errors.add(error);
            return this;
        }

        // Manejo de headers
        public Builder<T> headers(HttpHeaders headers) {
            this.headers = headers != null ? new HttpHeaders(headers) : null;
            return this;
        }

        public Builder<T> addHeader(String headerName, String headerValue) {
            if (this.headers == null) {
                this.headers = new HttpHeaders();
            }
            this.headers.add(headerName, headerValue);
            return this;
        }

        // Propiedades extras dinámicas
        public Builder<T> extraProperties(Map<String, Object> properties) {
            this.extraProperties = properties != null ? new LinkedHashMap<>(properties) : null;
            return this;
        }

        public Builder<T> addExtraProperty(String key, Object value) {
            if (this.extraProperties == null) {
                this.extraProperties = new LinkedHashMap<>();
            }
            this.extraProperties.put(key, value);
            return this;
        }

        // Configuración avanzada
        public Builder<T> withTimestampSupplier(Supplier<String> timestampSupplier) {
            this.timestampSupplier = Objects.requireNonNull(timestampSupplier);
            return this;
        }

        public ApiResponseBuilder<T> build() {
            return new ApiResponseBuilder<>(this);
        }
    }
}
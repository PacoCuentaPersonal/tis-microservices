package com.oauth2.app.oauth2_authorization_server.infrastructure.config.file;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Set;

@ConfigurationProperties(prefix = "app.file.upload")
public record FileUploadProperties(
        @DefaultValue("5242880") // 5MB en bytes
        long maxFileSize,

        @DefaultValue("1024") // 1KB en bytes
        long minFileSize,

        @DefaultValue("255")
        int maxFilenameLength,

        @DefaultValue("10")
        int maxFiles,

        Set<String> allowedMimeTypes,

        Set<String> allowedExtensions
) {
    public FileUploadProperties {
        if (maxFileSize <= 0) {
            throw new IllegalArgumentException("maxFileSize debe ser mayor que 0");
        }
        if (minFileSize <= 0) {
            throw new IllegalArgumentException("minFileSize debe ser mayor que 0");
        }
        if (minFileSize > maxFileSize) {
            throw new IllegalArgumentException("minFileSize no puede ser mayor que maxFileSize");
        }
        if (maxFilenameLength <= 0) {
            throw new IllegalArgumentException("maxFilenameLength debe ser mayor que 0");
        }
        if (maxFiles <= 0) {
            throw new IllegalArgumentException("maxFiles debe ser mayor que 0");
        }
        if (allowedMimeTypes == null || allowedMimeTypes.isEmpty()) {
            throw new IllegalArgumentException("allowedMimeTypes no puede estar vacío");
        }
        if (allowedExtensions == null || allowedExtensions.isEmpty()) {
            throw new IllegalArgumentException("allowedExtensions no puede estar vacío");
        }
    }

    // Métodos de utilidad adicionales (opcional)
    public long getMaxFileSizeInMB() {
        return maxFileSize / (1024 * 1024);
    }

    public long getMinFileSizeInKB() {
        return minFileSize / 1024;
    }
}
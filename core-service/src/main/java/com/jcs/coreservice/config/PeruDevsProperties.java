package com.jcs.coreservice.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "api.reniec.perudevs")
@Validated
public class PeruDevsProperties {
    @NotBlank
    private String url;
    @NotBlank private String token;
    // Getters y Setters

    public @NotBlank String getUrl() {
        return url;
    }

    public void setUrl(@NotBlank String url) {
        this.url = url;
    }

    public @NotBlank String getToken() {
        return token;
    }

    public void setToken(@NotBlank String token) {
        this.token = token;
    }
}
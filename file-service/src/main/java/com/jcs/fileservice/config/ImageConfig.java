package com.jcs.fileservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.image")
@Validated
public class ImageConfig {

    @NotNull
    private Long maxFileSize = 10 * 1024 * 1024L; // 10MB

    @Min(1) @Max(10000)
    private Integer maxWidth = 4096;

    @Min(1) @Max(10000)
    private Integer maxHeight = 4096;

    @Min(0) @Max(1)
    private Float defaultQuality = 0.85f;

    @Min(0) @Max(1)
    private Float thumbnailQuality = 0.75f;

    @NotNull
    private CacheConfig cache = new CacheConfig();

    @Data
    public static class CacheConfig {
        @Min(60) @Max(86400)
        private Long defaultTtl = 3600L;

        @Min(60) @Max(86400)
        private Long thumbnailTtl = 7200L;
    }
}
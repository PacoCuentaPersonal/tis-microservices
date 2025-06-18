package com.oauth2.app.oauth2_authorization_server.models.feing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageUploadResponse {
    private String imageId;
    private String message;
    private String url;
    private String thumbnailUrl;
    private Long originalSize;
    private Long compressedSize;
    private Double compressionRatio;
    private Instant uploadedAt;
    private ImageMetadata metadata;
}
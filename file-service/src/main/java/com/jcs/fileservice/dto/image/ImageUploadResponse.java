package com.jcs.fileservice.dto.image;

import com.jcs.fileservice.models.minio.ImageMetadata;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
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
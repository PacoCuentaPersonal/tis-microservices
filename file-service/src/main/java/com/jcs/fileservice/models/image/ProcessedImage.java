package com.jcs.fileservice.models.image;

import com.jcs.fileservice.models.minio.ImageMetadata;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessedImage {
    private byte[] data;
    private ImageMetadata metadata;
}
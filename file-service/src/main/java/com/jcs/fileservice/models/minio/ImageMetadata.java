package com.jcs.fileservice.models.minio;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class ImageMetadata {
    private Integer width;
    private Integer height;
    private String format;
    private Long size;
    private String colorSpace;
    private Integer bitDepth;
    private Map<String, Object> exifData;
}
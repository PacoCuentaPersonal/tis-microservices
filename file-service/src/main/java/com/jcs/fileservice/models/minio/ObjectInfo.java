package com.jcs.fileservice.models.minio;
import lombok.Builder;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@Builder
public class ObjectInfo {
    private String name;
    private long size;
    private ZonedDateTime lastModified;
    private String etag;
    private String contentType;
}
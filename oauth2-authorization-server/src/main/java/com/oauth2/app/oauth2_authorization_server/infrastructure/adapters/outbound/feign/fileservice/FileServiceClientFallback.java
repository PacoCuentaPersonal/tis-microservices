package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice;

import com.jcs.response.EnvelopeResponse;
import com.jcs.response.ResponseStatus;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.dto.ImageMetadata;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.dto.ImageUploadRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.dto.ImageUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component 
@Slf4j
public class FileServiceClientFallback implements IFileServiceClient {

    @Override
    public ResponseEntity<EnvelopeResponse<ImageUploadResponse>> uploadImage(ImageUploadRequest request) {
        log.error("Fallback: Unable to upload image to file service. Category: {}. File: {}", 
                  (request != null ? request.getNameImageCategory() : "N/A"), 
                  (request != null && request.getFile() != null ? request.getFile().getOriginalFilename() : "N/A"));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(EnvelopeResponse.<ImageUploadResponse>builder()
                        .status(ResponseStatus.ERROR)
                        .message("File service is unavailable. Could not upload image.")
                        .errors(Collections.emptyList())
                        .data((ImageUploadResponse) null) // Explicitly typed null
                        .build());
    }

    @Override
    public ResponseEntity<byte[]> getImage(String bucketName, String imageId, Integer width, Integer height) {
        log.error("Fallback: Unable to retrieve image {} from bucket {} from file service", imageId, bucketName);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @Override
    public ResponseEntity<Void> deleteImage(String bucketName, String imageId) {
        log.error("Fallback: Unable to delete image {} from bucket {} from file service", imageId, bucketName);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @Override
    public ResponseEntity<EnvelopeResponse<ImageMetadata>> getImageMetadata(String bucketName, String imageId) {
        log.error("Fallback: Unable to retrieve metadata for image {} from bucket {} from file service", imageId, bucketName);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(EnvelopeResponse.<ImageMetadata>builder()
                        .status(ResponseStatus.ERROR)
                        .message("File service is unavailable. Could not retrieve image metadata.")
                        .errors(Collections.emptyList())
                        .data((ImageMetadata) null) // Explicitly typed null
                        .build());
    }
}

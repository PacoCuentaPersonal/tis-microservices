package com.oauth2.app.oauth2_authorization_server.service.feing;

import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.interfaces.feing.IFileServiceClient;
import com.oauth2.app.oauth2_authorization_server.models.feing.ImageMetadata;
import com.oauth2.app.oauth2_authorization_server.models.feing.ImageUploadRequest;
import com.oauth2.app.oauth2_authorization_server.models.feing.ImageUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
public class FileServiceClientFallback implements IFileServiceClient {


    @Override
    public ResponseEntity<EnvelopeResponse<ImageUploadResponse>> uploadImage(ImageUploadRequest request) {
        log.error("Fallback: Unable to upload image to file service");


        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(EnvelopeResponse.success( null));

    }

    @Override
    public ResponseEntity<byte[]> getImage(String bucketName ,String imageId, Integer width, Integer height) {
        log.error("Fallback: Unable to retrieve image {} from file service", imageId);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @Override
    public ResponseEntity<Void> deleteImage(String bucketName,String imageId) {
        log.error("Fallback: Unable to delete image {} from file service", imageId);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @Override
    public ResponseEntity<EnvelopeResponse<ImageMetadata>> getImageMetadata(String bucketName, String imageId) {
        log.error("Fallback: Unable to retrieve metadata for image {} from file service", imageId);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(EnvelopeResponse.success(null));
    }
}
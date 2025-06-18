package com.oauth2.app.oauth2_authorization_server.interfaces.feing;

import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.config.feing.FeignMultipartConfig;
import com.oauth2.app.oauth2_authorization_server.models.feing.ImageMetadata;
import com.oauth2.app.oauth2_authorization_server.models.feing.ImageUploadRequest;
import com.oauth2.app.oauth2_authorization_server.models.feing.ImageUploadResponse;
import com.oauth2.app.oauth2_authorization_server.service.feing.FileServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "FILE-SERVICE",
        fallback = FileServiceClientFallback.class,
        configuration = FeignMultipartConfig.class,
        qualifiers = "fileServiceClient"
)
public interface IFileServiceClient {

    @PostMapping(value = "/api/v1/images/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EnvelopeResponse<ImageUploadResponse>> uploadImage(
            @ModelAttribute ImageUploadRequest request
    );

    @GetMapping("/api/v1/images/{bucketName}/{imageId}")
    ResponseEntity<byte[]> getImage(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("imageId") String imageId,
            @RequestParam(value = "w", required = false) Integer width,
            @RequestParam(value = "h", required = false) Integer height
    );

    @DeleteMapping("/api/v1/images/{bucketName}/{imageId}")
    ResponseEntity<Void> deleteImage(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("imageId") String imageId);

    @GetMapping("/api/v1/images/{bucketName}/{imageId}/metadata")
    ResponseEntity<EnvelopeResponse<ImageMetadata>> getImageMetadata(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("imageId") String imageId
    );





}
package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice;

import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.infrastructure.config.feing.FeignMultipartConfig; // Path corregido
// Imports actualizados a la nueva ubicación de DTOs de Feign
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.dto.ImageMetadata;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.dto.ImageUploadRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.dto.ImageUploadResponse;
// Import actualizado para el Fallback en el mismo paquete
// import com.oauth2.app.oauth2_authorization_server.service.feing.FileServiceClientFallback; // Original, será actualizado si FileServiceClientFallback se mueve aquí
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "FILE-SERVICE",
        fallback = FileServiceClientFallback.class, // Asumimos que FileServiceClientFallback estará en este paquete también
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

package com.jcs.fileservice.controller.v1;

import com.jcs.fileservice.models.minio.ImageMetadata;
import com.jcs.fileservice.dto.image.ImageUploadRequest;
import com.jcs.fileservice.dto.image.ImageUploadResponse;
import com.jcs.fileservice.service.interfaces.ImageService;
import com.jcs.response.EnvelopeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Validated
@Tag(name = "Image Management", description = "Image upload and processing endpoints")
public class ImageController {

    private final ImageService imageService;

    private static final int MIN_IMAGE_SIZE = 1;
    private static final int MAX_IMAGE_SIZE = 4096;
    private static final int CACHE_HOURS = 24;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an image", description = "Upload and convert image to WebP format")
    @ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "413", description = "File too large")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public CompletableFuture<ResponseEntity<EnvelopeResponse<ImageUploadResponse>>> uploadImage(
            @Valid @ModelAttribute ImageUploadRequest request) {

        log.info("Uploading image");
        return imageService.uploadImage(request)
                .thenApply(response -> ResponseEntity.ok(EnvelopeResponse.success(response)));
    }

    @GetMapping("/{bucketName}/{imageId}")
    @Operation(summary = "Get an image",
            description = "Retrieve image with optional resizing. This endpoint returns raw image data, not JSON.")
    @ApiResponse(responseCode = "200", description = "Image retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Image not found")
    public ResponseEntity<byte[]> getImage(
            @PathVariable String bucketName,
            @PathVariable String imageId,
            @Parameter(description = "Target width (1-4096)")
            @RequestParam(required = false) @Min(MIN_IMAGE_SIZE) @Max(MAX_IMAGE_SIZE) Integer w,
            @Parameter(description = "Target height (1-4096)")
            @RequestParam(required = false) @Min(MIN_IMAGE_SIZE) @Max(MAX_IMAGE_SIZE) Integer h) {

        log.debug("Retrieving image: {} with dimensions: {}x{}", imageId, w, h);

        byte[] imageData = imageService.getImage(bucketName,imageId, w, h);

        return ResponseEntity.ok()
                .headers(buildImageHeaders(imageData.length))
                .body(imageData);
    }

    @DeleteMapping("/{bucketName}/{imageId}")
    @Operation(summary = "Delete an image", description = "Remove image from storage and cache")
    @ApiResponse(responseCode = "204", description = "Image deleted successfully")
    @ApiResponse(responseCode = "404", description = "Image not found")
    public ResponseEntity<Void> deleteImage(
            @PathVariable String bucketName,
            @PathVariable String imageId) {
        log.info("Deleting image: {}", imageId);

        imageService.deleteImage(bucketName,imageId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bucketName}/{imageId}/metadata")
    @Operation(summary = "Get image metadata", description = "Retrieve image information")
    @ApiResponse(responseCode = "200", description = "Metadata retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Image not found")
    public ResponseEntity<EnvelopeResponse<ImageMetadata>> getImageMetadata(@PathVariable String bucketName,@PathVariable String imageId) {
        log.debug("Retrieving metadata for image: {}", imageId);

        ImageMetadata metadata = imageService.getImageMetadata(bucketName,imageId);

        return ResponseEntity.ok(EnvelopeResponse.success(metadata));
    }

    /**
     * Construye los headers para la respuesta de imagen
     * @param contentLength tamaño del contenido en bytes
     * @return HttpHeaders configurados
     */
    private HttpHeaders buildImageHeaders(long contentLength) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("image/webp"));
        headers.setCacheControl(CacheControl
                .maxAge(CACHE_HOURS, TimeUnit.HOURS)
                .cachePublic()
                .mustRevalidate()
                .getHeaderValue());
        headers.setContentLength(contentLength);
        headers.set("X-Content-Type-Options", "nosniff");
        return headers;
    }
}
package com.jcs.fileservice.service.impl;

import com.jcs.fileservice.exception.thrower.ImageNotFoundException;
import com.jcs.fileservice.exception.thrower.ImageProcessingException;
import com.jcs.fileservice.models.entity.ImageCategories;
import com.jcs.fileservice.models.minio.ImageMetadata;
import com.jcs.fileservice.dto.image.ImageUploadRequest;
import com.jcs.fileservice.dto.image.ImageUploadResponse;
import com.jcs.fileservice.models.image.ProcessedImage;
import com.jcs.fileservice.service.interfaces.*;
import com.jcs.fileservice.util.ImageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final IImageCategories imageCategories;

    private final ImageProcessorService imageProcessor;
    private final StorageService storageService;
    private final CacheService cacheService;
    private final ImageValidator imageValidator;
    private static final String THUMBNAIL_SUFFIX = "_thumb";
    private static final int THUMBNAIL_WIDTH = 200;

    @Value("${server.port}")
    private int port;
    @Value("${server.ssl.enabled:false}")
    private boolean secured;


    @Override
    @Async
    @Transactional
    public CompletableFuture<ImageUploadResponse> uploadImage(ImageUploadRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            ImageCategories byName = imageCategories.findByName(request.getNameImageCategory());
            String bucketName = byName.getName();
            try {
                imageValidator.validate(request.getFile());

                String imageId = UUID.randomUUID().toString();
                byte[] originalData = request.getFile().getBytes();
                long originalSize = originalData.length;

                // Procesar imagen principal
                ProcessedImage processed = imageProcessor.processImage(
                        originalData,
                        request.getQuality() / 100.0f
                );

                // Guardar imagen principal
                storageService.saveImage(bucketName,imageId, processed.getData());

                // Generar y guardar thumbnail si es necesario
                String thumbnailUrl = null;
                if (request.isGenerateThumbnail()) {
                    byte[] thumbnail = imageProcessor.resize(
                            processed.getData(),
                            THUMBNAIL_WIDTH,
                            null
                    );
                    storageService.saveImage(bucketName,imageId + THUMBNAIL_SUFFIX, thumbnail);
                    thumbnailUrl = buildImageUrl(bucketName,imageId + THUMBNAIL_SUFFIX);
                }

                // Construir respuesta
                return ImageUploadResponse.builder()
                        .imageId(imageId)
                        .message("Image uploaded successfully")
                        .url(buildImageUrl(bucketName,imageId))
                        .thumbnailUrl(thumbnailUrl)
                        .originalSize(originalSize)
                        .compressedSize((long) processed.getData().length)
                        .compressionRatio(calculateCompressionRatio(originalSize, processed.getData().length))
                        .uploadedAt(Instant.now())
                        .metadata(processed.getMetadata())
                        .build();

            } catch (Exception e) {
                log.error("Error processing upload", e);
                throw new ImageProcessingException("Failed to process image", e);
            }
        });
    }

    @Override
    public byte[] getImage(String bucketName, String imageId, Integer width, Integer height) {
        String cacheKey = buildCacheKey(imageId, width, height);

        return cacheService.get(cacheKey)
                .orElseGet(() -> {
                    byte[] original = storageService.getImage(bucketName,imageId)
                            .orElseThrow(() -> new ImageNotFoundException(imageId));
                    byte[] processed = null;
                    try {
                        processed = (width != null || height != null)
                                ? imageProcessor.resize(original, width, height)
                                : original;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Cachear resultado
                    cacheService.set(cacheKey, processed);

                    return processed;
                });
    }

    @Override
    @Transactional
    public void deleteImage(String bucketName,String imageId) {
        storageService.deleteImage(bucketName,imageId);
        storageService.deleteImage(bucketName,imageId + THUMBNAIL_SUFFIX);
        cacheService.evictAllVariations(imageId);
        log.info("Image deleted: {}", imageId);
    }

    @Override
    public ImageMetadata getImageMetadata(String bucketName,String imageId) {
        byte[] imageData = storageService.getImage(bucketName,imageId)
                .orElseThrow(() -> new ImageNotFoundException(imageId));

        try {
            return imageProcessor.extractMetadata(imageData);
        } catch (IOException e) {
            log.error("Failed to extract metadata for image: {}", imageId, e);
            throw new ImageProcessingException(
                    "Failed to extract metadata for image: " + imageId, e
            );
        }
    }

    private String buildImageUrl(String bucketName, String imageId) {
        String address = "localhost";
        if (secured){
            return String.format("https://%s:%s/api/v1/images/%s/%s", address, port,bucketName, imageId);
        }
        return String.format("http://%s:%s/api/v1/images/%s/%s", address, port,bucketName, imageId);
    }

    private String buildCacheKey(String imageId, Integer width, Integer height) {
        if (width == null && height == null) {
            return imageId;
        }
        return String.format("%s_%dx%d", imageId,
                width != null ? width : 0,
                height != null ? height : 0);
    }

    private double calculateCompressionRatio(long original, long compressed) {
        return Math.round((1.0 - (double) compressed / original) * 100 * 100) / 100.0;
    }
}
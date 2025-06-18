package com.jcs.fileservice.service.interfaces;

import com.jcs.fileservice.models.minio.ImageMetadata;
import com.jcs.fileservice.dto.image.ImageUploadRequest;
import com.jcs.fileservice.dto.image.ImageUploadResponse;

import java.util.concurrent.CompletableFuture;

public interface ImageService {
    CompletableFuture<ImageUploadResponse> uploadImage(ImageUploadRequest request);
    byte[] getImage(String bucketName,String imageId, Integer width, Integer height);
    void deleteImage(String bucketName,String imageId);
    ImageMetadata getImageMetadata(String bucketName,String imageId);
}
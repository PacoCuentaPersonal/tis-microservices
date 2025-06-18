package com.jcs.fileservice.service.impl;

import com.jcs.fileservice.models.minio.ObjectInfo;
import com.jcs.fileservice.service.interfaces.StorageService;
import com.jcs.fileservice.util.PagedResult;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.secure}")
    private boolean secure;

    private MinioClient minioClient;

    // Cache para evitar verificar la existencia del bucket en cada operación
    private final ConcurrentHashMap<String, Boolean> bucketCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            log.info("MinIO client initialized successfully. Endpoint: {}", endpoint);

        } catch (Exception e) {
            log.error("Failed to initialize MinIO client", e);
            throw new RuntimeException("MinIO initialization failed", e);
        }
    }

    /**
     * Verifica y crea el bucket si no existe
     */
    private void ensureBucketExists(String bucketName) {
        if (bucketCache.containsKey(bucketName)) {
            return;
        }

        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("Bucket '{}' created successfully", bucketName);
            }

            bucketCache.put(bucketName, true);

        } catch (Exception e) {
            log.error("Failed to ensure bucket exists: {}", bucketName, e);
            throw new RuntimeException("Failed to ensure bucket exists", e);
        }
    }

    @Override
    public void saveImage(String bucketName, String key, byte[] data) {
        ensureBucketExists(bucketName);

        String objectName = key + ".webp";

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(data), data.length, -1)
                            .contentType("image/webp")
                            .build()
            );

            log.info("Image saved successfully in bucket '{}': {} ({} bytes)",
                    bucketName, objectName, data.length);

        } catch (Exception e) {
            log.error("Failed to save image in bucket '{}': {}", bucketName, objectName, e);
            throw new RuntimeException("Failed to save image", e);
        }
    }

    @Override
    public Optional<byte[]> getImage(String bucketName, String key) {
        String objectName = key + ".webp";

        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            byte[] data = response.readAllBytes();
            log.debug("Image retrieved successfully from bucket '{}': {}, size: {} bytes",
                    bucketName, objectName, data.length);

            return Optional.of(data);

        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                log.warn("Image not found in bucket '{}': {}", bucketName, objectName);
                return Optional.empty();
            }
            log.error("Failed to retrieve image from bucket '{}': {}", bucketName, objectName, e);
            throw new RuntimeException("Failed to retrieve image", e);

        } catch (Exception e) {
            log.error("Failed to retrieve image from bucket '{}': {}", bucketName, objectName, e);
            throw new RuntimeException("Failed to retrieve image", e);
        }
    }

    @Override
    public void deleteImage(String bucketName, String key) {
        String objectName = key + ".webp";

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            log.info("Image deleted successfully from bucket '{}': {}", bucketName, objectName);

        } catch (Exception e) {
            log.error("Failed to delete image from bucket '{}': {}", bucketName, objectName, e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    @Override
    public PagedResult<String> listByPrefixPaginated(String bucketName, String prefix,
                                                     int pageSize, String startAfter) {
        List<String> objects = new ArrayList<>();
        String nextStartAfter = null;
        boolean hasMore = false;

        try {
            ListObjectsArgs.Builder listArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(true)
                    .maxKeys(pageSize + 1);

            if (startAfter != null && !startAfter.isEmpty()) {
                listArgs.startAfter(startAfter);
            }

            Iterable<Result<Item>> results = minioClient.listObjects(listArgs.build());

            int count = 0;
            for (Result<Item> result : results) {
                Item item = result.get();

                if (count < pageSize) {
                    objects.add(item.objectName());
                    nextStartAfter = item.objectName();
                } else {
                    hasMore = true;
                    break;
                }
                count++;
            }

            log.info("Listed {} objects from bucket '{}' with prefix: {}, hasMore: {}",
                    objects.size(), bucketName, prefix, hasMore);

        } catch (Exception e) {
            log.error("Failed to list objects from bucket '{}' with prefix: {}",
                    bucketName, prefix, e);
            throw new RuntimeException("Failed to list objects", e);
        }

        return PagedResult.<String>builder()
                .content(objects)
                .nextToken(hasMore ? nextStartAfter : null)
                .hasMore(hasMore)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public PagedResult<ObjectInfo> listByPrefixWithDetailsPaginated(
            String bucketName, String prefix, int pageSize, String startAfter) {

        List<ObjectInfo> objects = new ArrayList<>();
        String nextStartAfter = null;
        boolean hasMore = false;

        try {
            ListObjectsArgs.Builder listArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(true)
                    .includeUserMetadata(true)
                    .maxKeys(pageSize + 1);

            if (startAfter != null && !startAfter.isEmpty()) {
                listArgs.startAfter(startAfter);
            }

            Iterable<Result<Item>> results = minioClient.listObjects(listArgs.build());

            int count = 0;
            for (Result<Item> result : results) {
                Item item = result.get();

                if (count < pageSize) {
                    objects.add(ObjectInfo.builder()
                            .name(item.objectName())
                            .size(item.size())
                            .lastModified(item.lastModified())
                            .etag(item.etag())
                            .build());
                    nextStartAfter = item.objectName();
                } else {
                    hasMore = true;
                    break;
                }
                count++;
            }

        } catch (Exception e) {
            log.error("Failed to list objects with details from bucket '{}'", bucketName, e);
            throw new RuntimeException("Failed to list objects", e);
        }

        return PagedResult.<ObjectInfo>builder()
                .content(objects)
                .nextToken(hasMore ? nextStartAfter : null)
                .hasMore(hasMore)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public int deleteByPrefixBatch(String bucketName, String prefix, int batchSize) {
        PagedResult<String> page = listByPrefixPaginated(bucketName, prefix, batchSize, null);
        int totalDeleted = 0;

        for (String objectName : page.getContent()) {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
                totalDeleted++;
            } catch (Exception e) {
                log.error("Failed to delete object from bucket '{}': {}",
                        bucketName, objectName, e);
            }
        }

        log.info("Deleted {} objects in batch from bucket '{}' with prefix: {}",
                totalDeleted, bucketName, prefix);
        return totalDeleted;
    }

    @Override
    public List<String> listByPrefix(String bucketName, String prefix) {
        List<String> objects = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                objects.add(item.objectName());
            }

            log.info("Found {} objects in bucket '{}' with prefix: {}",
                    objects.size(), bucketName, prefix);

        } catch (Exception e) {
            log.error("Failed to list objects from bucket '{}' with prefix: {}",
                    bucketName, prefix, e);
            throw new RuntimeException("Failed to list objects", e);
        }

        return objects;
    }

    @Override
    public long countByPrefix(String bucketName, String prefix) {
        long count = 0;

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                result.get();
                count++;
            }

        } catch (Exception e) {
            log.error("Failed to count objects in bucket '{}' with prefix: {}",
                    bucketName, prefix, e);
            return 0;
        }

        return count;
    }

    /**
     * Método adicional para verificar si un bucket existe
     */
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to check if bucket exists: {}", bucketName, e);
            return false;
        }
    }

    /**
     * Método para limpiar el cache de buckets (útil para testing o mantenimiento)
     */
    public void clearBucketCache() {
        bucketCache.clear();
        log.info("Bucket cache cleared");
    }
}
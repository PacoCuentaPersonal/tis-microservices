package com.jcs.fileservice.service.interfaces;

import com.jcs.fileservice.models.minio.ObjectInfo;
import com.jcs.fileservice.util.PagedResult;

import java.util.List;
import java.util.Optional;

public interface StorageService {
    void saveImage(String bucketName, String key, byte[] data);
    Optional<byte[]> getImage(String bucketName, String key);
    void deleteImage(String bucketName, String key);
    PagedResult<String> listByPrefixPaginated(String bucketName, String prefix, int pageSize, String startAfter);
    PagedResult<ObjectInfo> listByPrefixWithDetailsPaginated(String bucketName, String prefix, int pageSize, String startAfter);
    int deleteByPrefixBatch(String bucketName, String prefix, int batchSize);
    List<String> listByPrefix(String bucketName, String prefix);
    long countByPrefix(String bucketName, String prefix);
}
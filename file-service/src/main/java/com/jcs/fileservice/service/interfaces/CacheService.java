package com.jcs.fileservice.service.interfaces;

import java.util.Optional;

public interface CacheService {
    void set(String key, byte[] data);
    void set(String key, byte[] data, long ttlSeconds);
    Optional<byte[]> get(String key);
    void evictAllVariations(String imageId);
}
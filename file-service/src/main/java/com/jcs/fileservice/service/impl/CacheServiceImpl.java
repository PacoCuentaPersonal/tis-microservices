package com.jcs.fileservice.service.impl;

import com.jcs.fileservice.service.interfaces.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, byte[]> redisTemplate;

    @Value("${app.image.cache.default-ttl}")
    private long defaultTtl;

    private static final String CACHE_PREFIX = "img:";

    @Override
    public void set(String key, byte[] data) {
        set(key, data, defaultTtl);
    }

    @Override
    public void set(String key, byte[] data, long ttlSeconds) {
        String cacheKey = CACHE_PREFIX + key;

        try {
            redisTemplate.opsForValue().set(
                    cacheKey,
                    data,
                    Duration.ofSeconds(ttlSeconds)
            );

            log.debug("Cached image: {}, size: {} KB, TTL: {}s",
                    cacheKey, data.length / 1024, ttlSeconds);

        } catch (Exception e) {
            log.warn("Failed to cache image: {}", cacheKey, e);
        }
    }

    @Override
    public Optional<byte[]> get(String key) {
        String cacheKey = CACHE_PREFIX + key;

        try {
            byte[] data = redisTemplate.opsForValue().get(cacheKey);

            if (data != null) {
                log.debug("Cache hit: {}, size: {} KB", cacheKey, data.length / 1024);
                return Optional.of(data);
            } else {
                log.debug("Cache miss: {}", cacheKey);
                return Optional.empty();
            }

        } catch (Exception e) {
            log.warn("Failed to get from cache: {}", cacheKey, e);
            return Optional.empty();
        }
    }

    @Override
    public void evictAllVariations(String imageId) {
        try {
            String pattern = CACHE_PREFIX + imageId + "*";
            Set<String> keys = redisTemplate.keys(pattern);

            if (keys != null && !keys.isEmpty()) {
                Long deleted = redisTemplate.delete(keys);
                log.info("Evicted {} cache entries for image: {}", deleted, imageId);
            }

        } catch (Exception e) {
            log.warn("Failed to evict variations for image: {}", imageId, e);
        }
    }
}
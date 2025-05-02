package com.jcs.authenticationservice.service.redis;

import com.jcs.authenticationservice.usecase.auth.ITokenStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenStorageServiceImpl implements ITokenStorageService {
    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void blacklistToken(String token, long ttl, TimeUnit unit) {
        if (token != null && !token.isEmpty()) {
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "invalid", ttl, unit);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
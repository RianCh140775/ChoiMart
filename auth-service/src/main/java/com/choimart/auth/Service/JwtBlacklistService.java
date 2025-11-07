package com.choimart.auth.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlacklistService {
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private final RedisTemplate<String, Object> redisTemplate;
    public void blacklistToken(String token, long expirationMillis){
        if (redisTemplate == null){
            log.warn("Redis not configured - blacklist not persisted.");
            return;
        }
        try {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX +token,
                    "true",
                    expirationMillis,
                    TimeUnit.MILLISECONDS
            );
            log.info("Token blacklisted for {} ms", expirationMillis);
        } catch (Exception e){
            log.error("Failed to add token to blacklist: {}", e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token){
        if (redisTemplate == null){
            log.debug("Redis not configured - skipping blacklist check.");
            return false;
        }

        try {
            Boolean exist = redisTemplate.hasKey(BLACKLIST_PREFIX + token);
            return Boolean.TRUE.equals(exist);
        } catch (Exception e) {
            log.warn("Redis unavailable while checking blacklist: {}", e.getMessage());
            return false;
        }
    }
}

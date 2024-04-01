package com.fz.admin.framework.redis.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Objects;

@EnableCaching
@Configuration
public class RedisCacheConfig {

    private static final Long REDIS_CACHE_DEFAULT_TTL = 3600L;

    @Bean
    public RedisCacheConfiguration defaultRedisCacheConfiguration(CacheProperties cacheProperties,
                                                                  @Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {


        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(Objects.requireNonNull(redisTemplate.getDefaultSerializer())))
                .entryTtl(Duration.ofSeconds(REDIS_CACHE_DEFAULT_TTL));

        return withCacheProperties(config, redisProperties);
    }


    private RedisCacheConfiguration withCacheProperties(RedisCacheConfiguration config, CacheProperties.Redis redisProperties) {

        config = config.computePrefixWith(name -> name);
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.computePrefixWith(name -> redisProperties.getKeyPrefix() + name);
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

}

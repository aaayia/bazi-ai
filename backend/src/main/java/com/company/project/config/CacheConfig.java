package com.company.project.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("baziAnalysis");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000) // Maximum 1000 entries
                .expireAfterWrite(24, TimeUnit.HOURS) // Cache for 24 hours
                .recordStats()); // Enable statistics
        return cacheManager;
    }
}

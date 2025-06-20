package br.dev.ricardocampos.silentguardapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Value("${cache.auth0.ttl-minutes}")
  private int ttlMinutes;

  @Value("${cache.auth0.max-size:1000}")
  private int maxSize;

  @Value("${cache.auth0.access-ttl-minutes:10}")
  private int accessTtlMinutes;

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("userInfoDto");
    cacheManager.setCaffeine(caffeineCacheBuilder());
    return cacheManager;
  }

  @Bean
  public Caffeine<Object, Object> caffeineCacheBuilder() {
    return Caffeine.newBuilder()
        // Size-based eviction
        .maximumSize(maxSize)

        // Time-based eviction (choose one or both)
        .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
        .expireAfterAccess(accessTtlMinutes, TimeUnit.MINUTES)

        // Performance and monitoring
        .recordStats() // Enable statistics
        .removalListener(
            (key, value, cause) -> {
              // Log when entries are removed (optional)
              System.out.println("🗑️ Cache entry removed: " + cause);
            });
  }
}

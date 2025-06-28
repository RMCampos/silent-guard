package br.dev.ricardocampos.silentguardapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for caching using Caffeine. This class sets up a cache manager with specified
 * time-to-live and maximum size for the cache.
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

  @Value("${cache.auth0.ttl-minutes}")
  private int ttlMinutes;

  @Value("${cache.auth0.max-size:1000}")
  private int maxSize;

  @Value("${cache.auth0.access-ttl-minutes:10}")
  private int accessTtlMinutes;

  /**
   * Configures a Caffeine cache manager with specified settings.
   *
   * @return a configured CacheManager instance.
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("userInfoDto");
    cacheManager.setCaffeine(caffeineCacheBuilder());
    return cacheManager;
  }

  /**
   * Builds a Caffeine cache configuration with size and time-based eviction policies.
   *
   * @return a Caffeine cache builder instance.
   */
  @Bean
  public Caffeine<Object, Object> caffeineCacheBuilder() {
    return Caffeine.newBuilder()
        .maximumSize(maxSize)
        .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
        .expireAfterAccess(accessTtlMinutes, TimeUnit.MINUTES)
        .recordStats()
        .removalListener(
            (key, value, cause) -> {
              log.info("Cache entry removed: {}", cause);
            });
  }
}

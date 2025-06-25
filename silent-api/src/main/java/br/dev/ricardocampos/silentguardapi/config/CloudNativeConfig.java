package br.dev.ricardocampos.silentguardapi.config;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@RegisterReflectionForBinding({
  com.github.benmanes.caffeine.cache.AsyncCacheLoader.class,
  com.github.benmanes.caffeine.cache.Cache.class,
  com.github.benmanes.caffeine.cache.Caffeine.class,
  com.github.benmanes.caffeine.cache.LoadingCache.class,
})
@ImportRuntimeHints({
  HttpServletRequestRuntimeHint.class,
})
public class CloudNativeConfig {}

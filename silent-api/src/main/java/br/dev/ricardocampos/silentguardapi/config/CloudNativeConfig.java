package br.dev.ricardocampos.silentguardapi.config;

import br.dev.ricardocampos.silentguardapi.dto.FieldIssueDto;
import br.dev.ricardocampos.silentguardapi.dto.MessageDto;
import br.dev.ricardocampos.silentguardapi.dto.UserInfoDto;
import io.micrometer.core.instrument.config.validate.ValidationException;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Configuration class for cloud-native features, including reflection hints and runtime hints. This
 * class is used to register classes for reflection and import runtime hints.
 */
@Configuration
@RegisterReflectionForBinding({
  com.github.benmanes.caffeine.cache.AsyncCacheLoader.class,
  com.github.benmanes.caffeine.cache.Cache.class,
  com.github.benmanes.caffeine.cache.Caffeine.class,
  com.github.benmanes.caffeine.cache.LoadingCache.class,
  FieldIssueDto.class,
  MessageDto.class,
  UserInfoDto.class,
  ValidationException.class,
})
@ImportRuntimeHints({
  HttpServletRequestRuntimeHint.class,
})
public class CloudNativeConfig {}

package br.dev.ricardocampos.silentguardapi.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Value("${cors.allowed-origins}")
  private String[] allowedOrigins;

  public void addCorsMappings(@NonNull CorsRegistry registry) {
    if (allowedOrigins != null && allowedOrigins.length > 0) {
      log.info("CORS policy allowed origins: {}", Arrays.asList(allowedOrigins));

      registry
          .addMapping("/**")
          .allowedOrigins(allowedOrigins)
          .allowedHeaders(
              "X-XSRF-TOKEN",
              "Content-Type",
              "Accept",
              "Authorization",
              "X-Frame-Options",
              "X-XSS-Protection",
              "Content-Security-Policy")
          .allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS", "HEAD", "PATCH");
    }
  }
}

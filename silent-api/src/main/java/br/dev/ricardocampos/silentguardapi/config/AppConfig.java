package br.dev.ricardocampos.silentguardapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

  private final String authDomain;

  public AppConfig(
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String authDomain) {
    this.authDomain = authDomain;
  }

  public String getAuthDomain() {
    return authDomain;
  }
}

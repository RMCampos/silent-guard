package br.dev.ricardocampos.silentguardapi.config;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application configuration properties for Silent Guard API. This class holds various configuration
 * values used throughout the application.
 */
@Getter
@ToString
@Component
@ConfigurationProperties(prefix = "br.dev.ricardocampos.silentguardapi")
public class AppConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String authZeroAuthDomain;

  @Value("${br.dev.ricardocampos.silentguardapi.target-env}")
  private String targetEnv;

  @Value("${br.dev.ricardocampos.silentguardapi.mailgun.api-key}")
  private String mailgunApiKey;

  @Value("${br.dev.ricardocampos.silentguardapi.mailgun.domain}")
  private String mailgunDomain;

  @Value("${br.dev.ricardocampos.silentguardapi.mailgun.sender-email}")
  private String mailgunSender;

  @Value("${br.dev.ricardocampos.silentguardapi.window-checking-interval}")
  private String windowCheckingInterval;

  @Value("${br.dev.ricardocampos.silentguardapi.dev-time-to-trigger}")
  private String devTimeToTrigger;
}

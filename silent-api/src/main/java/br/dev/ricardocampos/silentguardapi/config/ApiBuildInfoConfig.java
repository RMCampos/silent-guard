package br.dev.ricardocampos.silentguardapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/** This class contains a single component config with image tag version. */
@Component
public class ApiBuildInfoConfig implements HealthIndicator {

  @Value("${br.dev.ricardocampos.silentguardapi.version}")
  private String apiBuildInfo;

  /**
   * Returns the build information of the API.
   *
   * @return the build information as a string.
   */
  @Override
  public Health health() {
    return Health.up().withDetail("buildInfo", apiBuildInfo).build();
  }
}

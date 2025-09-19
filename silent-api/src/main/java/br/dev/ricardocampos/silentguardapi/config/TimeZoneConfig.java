package br.dev.ricardocampos.silentguardapi.config;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeZoneConfig {

  public static final String DEFAULT_TIMEZONE = "America/Sao_Paulo";

  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
  }
}

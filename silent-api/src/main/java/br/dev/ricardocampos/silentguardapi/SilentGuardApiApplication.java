package br.dev.ricardocampos.silentguardapi;

import br.dev.ricardocampos.silentguardapi.config.AppConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SilentGuardApiApplication {

  @Autowired private AppConfig appConfig;

  public static void main(String[] args) {
    SpringApplication.run(SilentGuardApiApplication.class, args);
  }

  @PostConstruct
  public void started() {
    log.info("API service loaded. Config loaded: {}", appConfig.toString());
  }
}

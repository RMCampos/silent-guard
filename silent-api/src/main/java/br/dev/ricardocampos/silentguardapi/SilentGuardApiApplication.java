package br.dev.ricardocampos.silentguardapi;

import br.dev.ricardocampos.silentguardapi.config.AppConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main application class for the Silent Guard API service. This class initializes the Spring
 * Boot application and logs the configuration upon startup.
 */
@Slf4j
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SilentGuardApiApplication {

  @Autowired private AppConfig appConfig;

  /**
   * The main method to run the Silent Guard API application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(SilentGuardApiApplication.class, args);
  }

  /**
   * This method is called after the application context is initialized. It logs that the API
   * service is ready and displays the loaded configuration.
   */
  @PostConstruct
  public void started() {
    log.info("API service is ready. Config loaded: {}", appConfig.toString());
  }
}

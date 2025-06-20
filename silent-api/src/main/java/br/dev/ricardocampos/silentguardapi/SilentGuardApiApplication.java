package br.dev.ricardocampos.silentguardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SilentGuardApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SilentGuardApiApplication.class, args);
  }
}

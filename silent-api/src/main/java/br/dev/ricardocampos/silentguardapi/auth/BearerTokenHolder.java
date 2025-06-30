package br.dev.ricardocampos.silentguardapi.auth;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Holder for the bearer token in the current request. This class is used to store and retrieve the
 * bearer token for the current request scope.
 */
@Getter
@Setter
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BearerTokenHolder {

  private String token;

  /**
   * Check if the bearer token is present and not blank.
   *
   * @return true if the token is present and not blank, false otherwise.
   */
  public boolean hasToken() {
    return !Objects.isNull(token) && !token.isBlank();
  }
}

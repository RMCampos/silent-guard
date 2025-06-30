package br.dev.ricardocampos.silentguardapi.auth;

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
}

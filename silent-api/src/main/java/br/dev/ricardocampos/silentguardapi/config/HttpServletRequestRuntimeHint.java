package br.dev.ricardocampos.silentguardapi.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.aot.hint.ProxyHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/** This class creates a RuntimeHint for the ServletRequest class. */
public class HttpServletRequestRuntimeHint implements RuntimeHintsRegistrar {

  /**
   * Registers runtime hints for the HttpServletRequest class to enable proxying.
   *
   * @param hints the RuntimeHints object to register hints with
   * @param classLoader the ClassLoader to use for loading classes, can be null
   */
  @Override
  public void registerHints(@NonNull RuntimeHints hints, @Nullable ClassLoader classLoader) {
    try {
      ProxyHints proxies = hints.proxies();
      proxies.registerJdkProxy(HttpServletRequest.class);
    } catch (Exception e) {
      throw new RuntimeException("Could not register RuntimeHint: " + e.getMessage());
    }
  }
}

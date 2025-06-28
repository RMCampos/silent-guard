package br.dev.ricardocampos.silentguardapi.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that adds the API build information to the response headers. This information can be used
 * for debugging or informational purposes.
 */
@Component
public class HeaderVersionFilter extends OncePerRequestFilter {

  @Value("${br.dev.ricardocampos.silentguardapi.version}")
  private String apiBuildInfo;

  /**
   * Filters the request and adds the API build information to the response headers.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain to continue processing the request
   * @throws ServletException if an error occurs during filtering
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    response.setHeader("X-BUILD-INFO", apiBuildInfo);
    filterChain.doFilter(request, response);
  }
}

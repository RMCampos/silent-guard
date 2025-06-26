package br.dev.ricardocampos.silentguardapi.filter;

import br.dev.ricardocampos.silentguardapi.auth.BearerTokenHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final BearerTokenHolder tokenHolder;

  public JwtAuthenticationFilter(BearerTokenHolder tokenHolder) {
    this.tokenHolder = tokenHolder;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    final String requestUri = request.getServletPath();
    if (requestUri.startsWith("/api/confirmation")) {
      filterChain.doFilter(request, response);
      return;
    }

    if (requestUri.startsWith("/actuator")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String authorizationHeader = request.getHeader("Authorization");

    if (Objects.isNull(authorizationHeader) || authorizationHeader.isBlank()) {
      SecurityContextHolder.clearContext();
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Unauthorized: Missing authentication token");
      return;
    }

    String jwtToken = authorizationHeader;

    if (authorizationHeader.startsWith("Bearer ")) {
      jwtToken = authorizationHeader.substring(7);
    }

    if (Objects.isNull(jwtToken) || jwtToken.isBlank()) {
      throw new ServletException("Invalid token");
    }

    tokenHolder.setToken(jwtToken);

    filterChain.doFilter(request, response);
  }
}

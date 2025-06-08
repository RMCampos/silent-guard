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

@Component
public class HeaderVersionFilter extends OncePerRequestFilter {

  @Value("${br.dev.ricardocampos.silentguardapi.version}")
  private String apiBuildInfo;

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

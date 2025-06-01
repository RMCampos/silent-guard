package br.com.ricardocampos.silentguardapi;

import java.util.Map;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  public Map<String, Object> getClaims() {
    return getJwtToken().getClaims();
  }

  public Optional<String> userEmail() {
    Jwt jwt = getJwtToken();
    return Optional.ofNullable(jwt.getClaimAsString("email"));
  }

  private Jwt getJwtToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (Jwt) authentication.getPrincipal();
  }
}

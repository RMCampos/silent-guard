package br.dev.ricardocampos.silentguardapi.controller;

import br.dev.ricardocampos.silentguardapi.dto.UserInfoDto;
import br.dev.ricardocampos.silentguardapi.service.AuthService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class Controller {

  private final AuthService authService;

  @GetMapping
  public Map<String, String> securedMethod() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Jwt jwt = (Jwt) authentication.getPrincipal();

    Map<String, String> map = new HashMap<>();

    Optional<UserInfoDto> userDto = authService.userEmail(jwt.getTokenValue());
    if (userDto.isEmpty()) {
      log.error("No user data found!");
      return map;
    }

    map.put("email", userDto.get().email());
    return map;
  }
}

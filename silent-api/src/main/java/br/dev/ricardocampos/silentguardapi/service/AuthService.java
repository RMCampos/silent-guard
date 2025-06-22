package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.config.AppConfig;
import br.dev.ricardocampos.silentguardapi.dto.UserInfoDto;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AuthService {

  private final AppConfig appConfig;

  public AuthService(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  @Cacheable(value = "userInfoDto", key = "#token")
  public Optional<UserInfoDto> getUserInfo(String token) {
    log.info(
        "Token length: {}, Hash: {}, Ends with: {}",
        token.length(),
        token.hashCode(),
        token.substring(token.length()-20));

    log.info("No cached version for the token, fetching from Auth0");
    String userInfoUrl = String.format("%s/userinfo", appConfig.getAuthZeroAuthDomain());

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    RestTemplate restTemplate = new RestTemplate();

    try {
      log.info("Starting request to {}", userInfoUrl);

      ResponseEntity<UserInfoDto> response =
          restTemplate.exchange(
              userInfoUrl, HttpMethod.GET, new HttpEntity<>(headers), UserInfoDto.class);

      log.info("Finished request to {}", userInfoUrl);
      log.debug("Response HTTP Status: {}", response.getStatusCode());
      log.debug("Response Body: {}", response.getBody());

      if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
        return getUserInfoAndValidate(response.getBody());
      }

    } catch (HttpClientErrorException e) {
      e.printStackTrace();
    }

    return Optional.empty();
  }

  private Optional<UserInfoDto> getUserInfoAndValidate(UserInfoDto userInfoDto) {
    if (Objects.isNull(userInfoDto)) {
      log.info("No user info found!");
      return Optional.empty();
    }

    log.debug("User info: {}", userInfoDto);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Jwt jwt = (Jwt) authentication.getPrincipal();

    String subFromJwt = jwt.getClaimAsString("sub");
    if (!subFromJwt.equals(userInfoDto.sub())) {
      throw new RuntimeException("User not authorized, invalid token!");
    }

    return Optional.of(userInfoDto);
  }
}

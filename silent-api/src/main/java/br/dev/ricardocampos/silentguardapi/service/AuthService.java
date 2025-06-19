package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.auth.BearerTokenHolder;
import br.dev.ricardocampos.silentguardapi.dto.UserInfoDto;
import br.dev.ricardocampos.silentguardapi.entity.UserEntity;
import br.dev.ricardocampos.silentguardapi.exception.InvalidUserException;
import br.dev.ricardocampos.silentguardapi.repository.UserRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  private final BearerTokenHolder tokenHolder;
  private final String authDomain;
  private final UserRepository userRepository;

  public AuthService(
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String authDomain,
      BearerTokenHolder tokenHolder,
      UserRepository userRepository) {
    this.tokenHolder = tokenHolder;
    this.authDomain = authDomain;
    this.userRepository = userRepository;
  }

  public Optional<UserEntity> getUserEntity() {
    Optional<UserInfoDto> userDto = getUserInfo();
    if (userDto.isEmpty()
        || Objects.isNull(userDto.get().email())
        || userDto.get().email().isBlank()) {
      throw new InvalidUserException();
    }

    return userRepository.findByEmail(userDto.get().email());
  }

  public Optional<UserInfoDto> getUserInfo() {
    String userInfoUrl = String.format("%s/userinfo", authDomain);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + tokenHolder.getToken());

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

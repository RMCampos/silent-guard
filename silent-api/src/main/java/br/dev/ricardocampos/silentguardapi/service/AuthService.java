package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.dto.UserInfoDto;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AuthService {

  public Optional<UserInfoDto> userEmail(String accessToken) {
    String userInfoUrl = "https://dev-aaa.us.auth0.com/userinfo";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);

    RestTemplate restTemplate = new RestTemplate();

    try {
      log.info("Starting request to {}", userInfoUrl);

      ResponseEntity<UserInfoDto> response =
          restTemplate.exchange(
              userInfoUrl, HttpMethod.GET, new HttpEntity<>(headers), UserInfoDto.class);

      log.info("Finished request to {}", userInfoUrl);

      if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
        UserInfoDto userInfoDto = response.getBody();
        log.info("User info: {}", userInfoDto);
        return Optional.ofNullable(userInfoDto);
      }

    } catch (HttpClientErrorException e) {
      e.printStackTrace();
    }

    return Optional.empty();
  }
}

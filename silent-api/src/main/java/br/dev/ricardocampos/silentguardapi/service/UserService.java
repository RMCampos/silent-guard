package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.auth.BearerTokenHolder;
import br.dev.ricardocampos.silentguardapi.dto.UserInfoDto;
import br.dev.ricardocampos.silentguardapi.entity.UserEntity;
import br.dev.ricardocampos.silentguardapi.exception.InvalidUserException;
import br.dev.ricardocampos.silentguardapi.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user-related operations, including user registration and sign-in. This
 * service checks if a user is registered and updates their last check-in time.
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final AuthService authService;

  private final BearerTokenHolder bearerTokenHolder;

  /**
   * Checks if the user is registered or signs them up if they are not. If the user is already
   * registered, it updates their last check-in time.
   *
   * @throws InvalidUserException if the user information is invalid or missing.
   */
  public void signUpOrSignUser() {
    Optional<UserInfoDto> userDto = authService.getUserInfo(bearerTokenHolder.getToken());
    if (userDto.isEmpty()
        || Objects.isNull(userDto.get().email())
        || userDto.get().email().isBlank()) {
      throw new InvalidUserException();
    }

    log.info("Checking signUp or SignIn for user {}", userDto.get().email());

    Optional<UserEntity> userOptional = userRepository.findByEmail(userDto.get().email());
    if (userOptional.isEmpty()) {
      UserEntity user = new UserEntity();
      user.setEmail(userDto.get().email());
      user.setLastCheckIn(LocalDateTime.now());
      user.setCreatedAt(LocalDateTime.now());

      userRepository.save(user);
      log.info("User registered successfully!");
      return;
    }

    UserEntity user = userOptional.get();
    user.setLastCheckIn(LocalDateTime.now());

    userRepository.save(user);
    log.info("User logged in successfully!");
  }
}

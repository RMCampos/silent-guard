package br.dev.ricardocampos.silentguardapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a user is invalid. This exception is used to indicate that the user
 * associated with a request is not valid, such as when the user does not exist or has not been
 * properly authenticated.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUserException extends ResponseStatusException {

  /**
   * Constructs a new InvalidUserException with a default message indicating that the user is
   * invalid.
   */
  public InvalidUserException() {
    super(HttpStatus.BAD_REQUEST, "Invalid user!");
  }
}

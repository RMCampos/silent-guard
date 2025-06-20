package br.dev.ricardocampos.silentguardapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUserException extends ResponseStatusException {

  public InvalidUserException() {
    super(HttpStatus.BAD_REQUEST, "Invalid user!");
  }
}

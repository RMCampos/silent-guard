package br.dev.ricardocampos.silentguardapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends ResponseStatusException {

  public MessageNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Message not found!");
  }
}

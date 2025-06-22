package br.dev.ricardocampos.silentguardapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/** This exception represents an error when sending email messages. */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class MailServiceException extends ResponseStatusException {

  public MailServiceException(String error) {
    super(HttpStatus.SERVICE_UNAVAILABLE, error);
  }
}

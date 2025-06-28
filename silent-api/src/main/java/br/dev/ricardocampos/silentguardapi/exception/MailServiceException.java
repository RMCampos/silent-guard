package br.dev.ricardocampos.silentguardapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when there is an issue with the mail service, such as when the service is
 * unavailable. This exception is used to indicate that the application cannot send emails at the
 * moment.
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class MailServiceException extends ResponseStatusException {

  /**
   * Constructs a new MailServiceException with a default message indicating that the mail service
   * is unavailable.
   */
  public MailServiceException(String error) {
    super(HttpStatus.SERVICE_UNAVAILABLE, error);
  }
}

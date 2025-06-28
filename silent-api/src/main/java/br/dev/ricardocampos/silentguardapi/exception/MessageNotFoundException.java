package br.dev.ricardocampos.silentguardapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a message is not found. This exception is used to indicate that a requested
 * message does not exist in the system, such as when trying to retrieve or update a message that
 * has been deleted or never existed.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends ResponseStatusException {

  /**
   * Constructs a new MessageNotFoundException with a default message indicating that the message
   * was not found.
   */
  public MessageNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Message not found!");
  }
}

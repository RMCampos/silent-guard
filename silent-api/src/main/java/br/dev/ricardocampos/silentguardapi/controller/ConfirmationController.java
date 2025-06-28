package br.dev.ricardocampos.silentguardapi.controller;

import br.dev.ricardocampos.silentguardapi.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user confirmation actions, such as check-ins based on a confirmation
 * string. This controller provides an endpoint to register a user's check-in.
 */
@RestController
@RequestMapping("/api/confirmation")
@AllArgsConstructor
public class ConfirmationController {

  private final MessageService messageService;

  /**
   * Endpoint to register a user check-in based on a confirmation string.
   *
   * @param confirmation the confirmation string provided by the user
   * @return a ResponseEntity with no content if the check-in is successful
   */
  @PutMapping("/check-in/{confirmation}")
  public ResponseEntity<Void> userCheckIn(@PathVariable("confirmation") String confirmation) {
    messageService.registerUserCheckIn(confirmation);
    return ResponseEntity.noContent().build();
  }
}

package br.dev.ricardocampos.silentguardapi.controller;

import br.dev.ricardocampos.silentguardapi.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/confirmation")
@AllArgsConstructor
public class ConfirmationController {

  private final MessageService messageService;

  @PutMapping("/check-in/{confirmation}")
  public ResponseEntity<Void> userCheckIn(@PathVariable("confirmation") String confirmation) {
    messageService.registerUserCheckIn(confirmation);
    return ResponseEntity.noContent().build();
  }
}

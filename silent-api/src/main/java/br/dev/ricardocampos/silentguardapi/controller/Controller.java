package br.dev.ricardocampos.silentguardapi.controller;

import br.dev.ricardocampos.silentguardapi.dto.MessageDto;
import br.dev.ricardocampos.silentguardapi.service.MessageService;
import br.dev.ricardocampos.silentguardapi.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class Controller {

  private final UserService userService;

  private final MessageService messageService;

  @GetMapping("/messages")
  public ResponseEntity<List<MessageDto>> getMessages() {
    return ResponseEntity.ok(messageService.getMessages());
  }

  @PutMapping("/messages")
  public ResponseEntity<Void> updateMessage(@RequestBody MessageDto messageDto) {
    messageService.updateMessage(messageDto);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/user")
  public ResponseEntity<Void> userSignedUpOrIn() {
    userService.signUpOrSignUser();
    return ResponseEntity.noContent().build();
  }
}

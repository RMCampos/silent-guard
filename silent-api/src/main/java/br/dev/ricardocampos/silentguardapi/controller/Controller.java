package br.dev.ricardocampos.silentguardapi.controller;

import br.dev.ricardocampos.silentguardapi.dto.MessageDto;
import br.dev.ricardocampos.silentguardapi.service.MessageService;
import br.dev.ricardocampos.silentguardapi.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling message-related operations, including creating, updating, deleting, and
 * retrieving messages. It also handles user sign-up or sign-in actions.
 */
@Slf4j
@RestController
@RequestMapping("/api/messages")
@AllArgsConstructor
public class Controller {

  private final UserService userService;

  private final MessageService messageService;

  /**
   * Retrieves a list of all messages.
   *
   * @return a ResponseEntity containing a list of MessageDto objects
   */
  @GetMapping
  public ResponseEntity<List<MessageDto>> getMessages() {
    return ResponseEntity.ok(messageService.getMessages());
  }

  /**
   * Retrieves a specific message by its ID.
   *
   * @param id the ID of the message to retrieve
   * @return a ResponseEntity containing the MessageDto object
   */
  @PutMapping
  public ResponseEntity<MessageDto> createMessage(@Valid @RequestBody MessageDto messageDto) {
    return ResponseEntity.ok(messageService.createMessage(messageDto));
  }

  /**
   * Creates a new message.
   *
   * @param messageDto the message data transfer object containing the message details
   * @return a ResponseEntity containing the created MessageDto object
   */
  @PostMapping("/{id}")
  public ResponseEntity<Void> updateMessage(
      @RequestBody MessageDto messageDto, @PathVariable("id") Long id) {
    messageService.updateMessage(id, messageDto);
    return ResponseEntity.noContent().build();
  }

  /**
   * Deletes a message by its ID.
   *
   * @param id the ID of the message to delete
   * @return a ResponseEntity with no content if the deletion is successful
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long id) {
    messageService.deleteMessage(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Handles user sign-up or sign-in actions.
   *
   * @return a ResponseEntity with no content if the action is successful
   */
  @PostMapping("/user")
  public ResponseEntity<Void> userSignedUpOrIn() {
    userService.signUpOrSignUser();
    return ResponseEntity.noContent().build();
  }
}

package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.auth.BearerTokenHolder;
import br.dev.ricardocampos.silentguardapi.dto.MessageDto;
import br.dev.ricardocampos.silentguardapi.dto.UserInfoDto;
import br.dev.ricardocampos.silentguardapi.entity.MessageEntity;
import br.dev.ricardocampos.silentguardapi.entity.UserEntity;
import br.dev.ricardocampos.silentguardapi.exception.InvalidUserException;
import br.dev.ricardocampos.silentguardapi.exception.MessageNotFoundException;
import br.dev.ricardocampos.silentguardapi.repository.MessageRepository;
import br.dev.ricardocampos.silentguardapi.repository.UserRepository;
import br.dev.ricardocampos.silentguardapi.util.UuidUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service class for managing messages. This service provides methods to retrieve, create, update,
 * and delete messages for a user. It also handles user check-ins and scheduling reminders.
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageService {

  private final UserRepository userRepository;

  private final MessageRepository messageRepository;

  private final AuthService authService;

  private final BearerTokenHolder bearerTokenHolder;

  private final PersistentReminderService persistentReminderService;

  /**
   * Retrieves all messages for the authenticated user.
   *
   * @return a list of MessageDto objects representing the user's messages.
   */
  public List<MessageDto> getMessages() {
    UserEntity user = getUserEntity();
    log.info("Getting all messages for user {}", user.getId());

    List<MessageEntity> messageList = messageRepository.findAllByUserId(user.getId());
    log.info("{} message(s) found.", messageList.size());

    return messageList.stream().map(MessageDto::fromEntity).toList();
  }

  /**
   * Creates a new message for the authenticated user.
   *
   * @param messageDto the MessageDto containing the details of the message to be created.
   * @return the created MessageDto.
   */
  @Transactional
  public MessageDto createMessage(MessageDto messageDto) {
    UserEntity user = getUserEntity();
    log.info("Creating message for user {}", user.getId());

    Set<String> uniqueEmails = new HashSet<>(messageDto.recipients());

    String targets = uniqueEmails.stream().map(String::trim).collect(Collectors.joining(";"));
    MessageEntity message = new MessageEntity();
    message.setUserId(user.getId());
    message.setSubject(messageDto.subject());
    message.setTargets(targets);
    message.setContent(messageDto.content());
    message.setSpanDays(messageDto.daysToTrigger());
    message.setCreatedAt(LocalDateTime.now());
    message.setLastReminderSent(null);
    message.setNextReminderDue(LocalDateTime.now().plusDays(messageDto.daysToTrigger()));
    message.setReminderUuid(new UuidUtil().generateRecipientUuid(targets));

    messageRepository.save(message);
    persistentReminderService.scheduleCheckingMessage(message);

    log.info("Message created for user {}", user.getId());

    return MessageDto.fromEntity(message);
  }

  /**
   * Updates an existing message for the authenticated user.
   *
   * @param id the ID of the message to be updated
   * @param messageDto the MessageDto containing the updated details of the message
   */
  @Transactional
  public void updateMessage(Long id, MessageDto messageDto) {
    UserEntity user = getUserEntity();
    log.info("Updating message for user {}", user.getId());

    Optional<MessageEntity> messageOptional = messageRepository.findById(id);
    if (messageOptional.isEmpty()) {
      throw new MessageNotFoundException();
    }

    String targets =
        messageDto.recipients().stream().map(String::trim).collect(Collectors.joining(";"));
    MessageEntity messageFromDb = messageOptional.get();

    persistentReminderService.cancelExistingTask(id, true);
    persistentReminderService.cancelExistingTask(id, false);

    messageFromDb.setSubject(messageDto.subject());
    messageFromDb.setTargets(targets);
    messageFromDb.setContent(messageDto.content());
    messageFromDb.setSpanDays(messageDto.daysToTrigger());
    messageFromDb.setUpdatedAt(LocalDateTime.now());
    messageFromDb.setDisabledAt(null);
    messageFromDb.setLastReminderSent(null);
    messageFromDb.setNextReminderDue(LocalDateTime.now().plusDays(messageDto.daysToTrigger()));
    messageFromDb.setReminderUuid(new UuidUtil().generateRecipientUuid(targets));

    if (!messageDto.active()) {
      messageFromDb.setDisabledAt(LocalDateTime.now());
    }

    messageRepository.save(messageFromDb);

    log.info("Message updated for user {}", user.getId());

    if (messageDto.active()) {
      persistentReminderService.scheduleCheckingMessage(messageFromDb);
    }
  }

  /**
   * Deletes a message by its ID for the authenticated user.
   *
   * @param id the ID of the message to be deleted
   */
  @Transactional
  public void deleteMessage(Long id) {
    UserEntity user = getUserEntity();
    log.info("Deleting message for user {}", user.getId());

    Optional<MessageEntity> messageOptional = messageRepository.findById(id);
    if (messageOptional.isEmpty()) {
      throw new MessageNotFoundException();
    }

    messageRepository.delete(messageOptional.get());
    log.info("Message deleted for user {}", user.getId());

    persistentReminderService.cancelExistingTask(id, true);
    persistentReminderService.cancelExistingTask(id, false);
    log.info("Disabled schedule engine for message id {}", id);
  }

  /**
   * Registers a user check-in for a specific confirmation ID.
   *
   * @param confirmation the confirmation ID of the message to register the check-in for
   */
  @Transactional
  public void registerUserCheckIn(String confirmation) {
    try {
      log.info("Registering user check-in for confirmation id {}", confirmation);
      Optional<MessageEntity> messageOption =
          messageRepository.findByReminderUuid(UUID.fromString(confirmation));
      if (messageOption.isEmpty()) {
        log.info("Message not found for the confirmation id {}", confirmation);
        return;
      }

      MessageEntity message = messageOption.get();
      message.setLastCheckIn(LocalDateTime.now());
      message.setUpdatedAt(LocalDateTime.now());

      messageRepository.save(message);

      persistentReminderService.cancelExistingTask(message.getId(), true);
      log.info("Content message successfully canceled upon check in.");
    } catch (Exception e) {
      log.error("Error when registering user check in {}", e.getMessage());
    }
  }

  private UserEntity getUserEntity() {
    Optional<UserInfoDto> userDto = authService.getUserInfo(bearerTokenHolder.getToken());
    if (userDto.isEmpty()
        || Objects.isNull(userDto.get().email())
        || userDto.get().email().isBlank()) {
      throw new InvalidUserException();
    }

    Optional<UserEntity> userOptional = userRepository.findByEmail(userDto.get().email());
    if (userOptional.isEmpty()) {
      log.error("User not found for the authenticated token");
      throw new InvalidUserException();
    }
    return userOptional.get();
  }
}

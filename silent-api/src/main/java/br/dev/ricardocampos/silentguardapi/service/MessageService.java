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

@Slf4j
@Service
@AllArgsConstructor
public class MessageService {

  private final UserRepository userRepository;

  private final MessageRepository messageRepository;

  private final AuthService authService;

  private final BearerTokenHolder bearerTokenHolder;

  private final PersistentReminderService persistentReminderService;

  public List<MessageDto> getMessages() {
    Optional<UserEntity> user = getUserEntity();
    log.info("Getting all messages for user {}", user.get().getId());

    List<MessageEntity> messageList = messageRepository.findAllByUserId(user.get().getId());
    log.info("{} message(s) found.", messageList.size());

    return messageList.stream().map(MessageDto::fromEntity).toList();
  }

  @Transactional
  public MessageDto createMessage(MessageDto messageDto) {
    Optional<UserEntity> user = getUserEntity();
    log.info("Creating message for user {}", user.get().getId());

    Set<String> uniqueEmails = new HashSet<>();
    uniqueEmails.addAll(messageDto.recipients());

    String targets = uniqueEmails.stream().map(String::trim).collect(Collectors.joining(";"));
    MessageEntity message = new MessageEntity();
    message.setUserId(user.get().getId());
    message.setTitle(messageDto.title());
    message.setTargets(targets);
    message.setContent(messageDto.content());
    message.setSpanDays(messageDto.daysToTrigger());
    message.setCreatedAt(LocalDateTime.now());
    message.setLastReminderSent(null);
    message.setNextReminderDue(LocalDateTime.now().plusDays(messageDto.daysToTrigger()));
    message.setReminderUuid(new UuidUtil().generateRecipientUuid(targets));

    messageRepository.save(message);
    persistentReminderService.scheduleCheckingMessage(message);

    log.info("Message created for user {}", user.get().getId());

    return MessageDto.fromEntity(message);
  }

  @Transactional
  public void updateMessage(Long id, MessageDto messageDto) {
    Optional<UserEntity> user = getUserEntity();
    log.info("Updating message for user {}", user.get().getId());

    Optional<MessageEntity> messageOptional = messageRepository.findById(id);
    if (messageOptional.isEmpty()) {
      throw new MessageNotFoundException();
    }

    String targets =
        messageDto.recipients().stream().map(String::trim).collect(Collectors.joining(";"));
    MessageEntity messageFromDb = messageOptional.get();

    persistentReminderService.cancelExistingTask(id, true);
    persistentReminderService.cancelExistingTask(id, false);

    messageFromDb.setTitle(messageDto.title());
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

    log.info("Message updated for user {}", user.get().getId());

    if (messageDto.active()) {
      persistentReminderService.scheduleCheckingMessage(messageFromDb);
    }
  }

  @Transactional
  public void deleteMessage(Long id) {
    Optional<UserEntity> user = getUserEntity();
    log.info("Deleting message for user {}", user.get().getId());

    Optional<MessageEntity> messageOptional = messageRepository.findById(id);
    if (messageOptional.isEmpty()) {
      throw new MessageNotFoundException();
    }

    messageRepository.delete(messageOptional.get());
    log.info("Message updated for user {}", user.get().getId());

    persistentReminderService.cancelExistingTask(id, true);
    persistentReminderService.cancelExistingTask(id, false);
    log.info("Disabled schedule engine for message id {}", id);
  }

  public void registerUserCheckIn(String confirmation) {
    try {
      log.info("Registering user check-in for confirmation id {}", confirmation);
      Optional<MessageEntity> messageOption =
          messageRepository.findByReminderUuid(UUID.fromString(confirmation));
      if (messageOption.isEmpty()) {
        log.info("Message not found for the confirmation id {}", confirmation);
        return;
      }

      persistentReminderService.cancelExistingTask(messageOption.get().getId(), true);
      log.info("Content message successfully canceled upon check in.");
    } catch (Exception e) {
      log.error("Error when registering user check in {}", e.getMessage());
    }
  }

  private Optional<UserEntity> getUserEntity() {
    Optional<UserInfoDto> userDto = authService.getUserInfo(bearerTokenHolder.getToken());
    if (userDto.isEmpty()
        || Objects.isNull(userDto.get().email())
        || userDto.get().email().isBlank()) {
      throw new InvalidUserException();
    }

    return userRepository.findByEmail(userDto.get().email());
  }
}

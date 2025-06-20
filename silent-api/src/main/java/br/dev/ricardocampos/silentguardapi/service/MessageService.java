package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.dto.MessageDto;
import br.dev.ricardocampos.silentguardapi.entity.MessageEntity;
import br.dev.ricardocampos.silentguardapi.entity.UserEntity;
import br.dev.ricardocampos.silentguardapi.exception.MessageNotFoundException;
import br.dev.ricardocampos.silentguardapi.repository.MessageRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;

  private final AuthService authService;

  public List<MessageDto> getMessages() {
    Optional<UserEntity> user = authService.getUserEntity();
    log.info("Getting all messages for user {}", user.get().getId());

    List<MessageEntity> messageList = messageRepository.findAllByUserId(user.get().getId());
    log.info("{} message(s) found.");

    return messageList.stream().map(MessageDto::fromEntity).toList();
  }

  public void updateMessage(MessageDto messageDto) {
    Optional<UserEntity> user = authService.getUserEntity();
    log.info("Updating message for user {}", user.get().getId());

    Optional<MessageEntity> messageOptional = messageRepository.findById(messageDto.id());
    if (messageOptional.isEmpty()) {
      throw new MessageNotFoundException();
    }

    MessageEntity messageFromDb = messageOptional.get();
    messageFromDb.setTitle(messageDto.title());
    messageFromDb.setTargets(messageDto.recipient());
    messageFromDb.setContent(messageDto.content());
    messageFromDb.setSpanDays(messageDto.daysToTrigger());
    messageFromDb.setUpdatedAt(LocalDateTime.now());

    log.info("Message updated for user {}", user.get().getId());
  }
}

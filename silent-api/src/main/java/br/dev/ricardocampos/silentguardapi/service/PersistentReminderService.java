package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.entity.MessageEntity;
import br.dev.ricardocampos.silentguardapi.repository.MessageRepository;
import io.jsonwebtoken.lang.Arrays;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PersistentReminderService {

  private final TaskScheduler taskScheduler;

  private final MessageRepository messageRepository;

  private final MailgunEmailService mailgunEmailService;

  private static final Map<Long, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();

  @PostConstruct
  public void restoreSchedulesOnStartup() {
    log.info("Restoring scheduled reminders, if any");
    List<MessageEntity> activeReminders = messageRepository.findByDisabledAtNull();

    for (MessageEntity message : activeReminders) {
      scheduleCheckingMessage(message);
    }

    log.info("Restored {} scheduled reminders on startup", activeReminders.size());
  }

  public void scheduleCheckingMessage(MessageEntity message) {
    // TODO: handle update or existing messages/schedules

    // Calculate delay until next reminder
    Duration initialDelay = Duration.between(LocalDateTime.now(), message.getNextReminderDue());
    Duration interval = Duration.ofDays(message.getSpanDays());
    if (initialDelay.isNegative()) {
      initialDelay = Duration.ZERO; // Send immediately if overdue
    }

    log.info(
        "Scheduling check-in message {} to be sent in {}, then repeat after {} days",
        message.getId(),
        formatDuration(initialDelay),
        message.getSpanDays());

    ScheduledFuture<?> future =
        taskScheduler.scheduleWithFixedDelay(
            () -> handleReminderAndUpdateDb(message), Instant.now().plus(initialDelay), interval);

    activeTasks.put(message.getId(), future);
  }

  private void handleReminderAndUpdateDb(MessageEntity message) {
    try {
      List<String> recipients = Arrays.asList(message.getTargets().split(";"));
      mailgunEmailService.sendCheckInRequest(recipients, message.getReminderUuid().toString());

      // Update database with last sent time
      messageRepository
          .findById(message.getId())
          .ifPresent(
              reminder -> {
                reminder.setLastReminderSent(LocalDateTime.now());
                reminder.setNextReminderDue(LocalDateTime.now().plusDays(reminder.getSpanDays()));
                messageRepository.saveAndFlush(reminder);
              });

      // Schedule HTML content to be sent if not confirmed
      scheduleContentMessage(message);

    } catch (Exception e) {
      log.error("Failed to send reminder for message id {}", message.getId(), e);
    }
  }

  public void scheduleContentMessage(MessageEntity message) {
    // TODO: handle update or existing messages/schedules

    Duration initialDelay =
        Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(12));

    log.info(
        "Scheduling check-in message {} to be sent in {}, then repeat after {} days",
        message.getId(),
        formatDuration(initialDelay),
        message.getSpanDays());

    ScheduledFuture<?> future =
        taskScheduler.schedule(
            () -> handleContentReminderAndUpdateDb(message), Instant.now().plus(initialDelay));

    activeTasks.put(message.getId(), future);
  }

  private void handleContentReminderAndUpdateDb(MessageEntity message) {
    try {
      // get last_check_in
      // if time between last checking and now, send email
      Optional<MessageEntity> messageOpt = messageRepository.findById(message.getId());

      // keep going from here

      List<String> recipients = Arrays.asList(message.getTargets().split(";"));
      mailgunEmailService.sendHtmlContentMessage(recipients, message.getContent());

      // Update database with last sent time
      messageRepository
          .findById(message.getId())
          .ifPresent(
              reminder -> {
                reminder.setLastReminderSent(LocalDateTime.now());
                reminder.setNextReminderDue(LocalDateTime.now().plusDays(reminder.getSpanDays()));
                messageRepository.saveAndFlush(reminder);
              });

      // Schedule HTML content to be sent if not confirmed
      scheduleContentMessage(message);

    } catch (Exception e) {
      log.error("Failed to send reminder for message id {}", message.getId(), e);
    }
  }

  public void cancelExistingTask(Long messageId) {
    log.info("Canceling existing task: {}", messageId);
    ScheduledFuture<?> existingTask = activeTasks.remove(messageId);

    if (existingTask != null) {
      boolean cancelled = existingTask.cancel(false); // false = don't interrupt if running

      if (cancelled) {
        log.info("Successfully cancelled existing reminder task for message {}", messageId);
      } else {
        log.warn(
            "Failed to cancel existing reminder task for message {} - task may have already"
                + " completed",
            messageId);
      }
    } else {
      log.debug("No existing task found for message {} to cancel", messageId);
    }
  }

  private String formatDuration(Duration duration) {
    if (duration == null) {
      return "0 seconds";
    }

    long totalSeconds = duration.getSeconds();
    long days = totalSeconds / 86400;
    long hours = (totalSeconds % 86400) / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;

    StringBuilder result = new StringBuilder();

    if (days > 0) result.append(days).append("d ");
    if (hours > 0) result.append(hours).append("h ");
    if (minutes > 0) result.append(minutes).append("m ");
    if (seconds > 0 || result.length() == 0) result.append(seconds).append("s");

    return result.toString().trim();
  }
}

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
import java.util.Objects;
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

  private static final Map<String, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();

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

    // Prod:
    // Duration initialDelay = Duration.between(LocalDateTime.now(), message.getNextReminderDue());

    // Dev:
    Duration initialDelay =
        Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(5));

    // Prod:
    // Duration interval = Duration.ofDays(message.getSpanDays());

    // Dev:
    Duration interval = Duration.ofMinutes(5);

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

    activeTasks.put(createScheduleId(message.getId(), false), future);
  }

  private void handleReminderAndUpdateDb(MessageEntity message) {
    try {
      log.info("Handling check-in message schedule for message id {}", message.getId());
      List<String> recipients = Arrays.asList(message.getTargets().split(";"));
      mailgunEmailService.sendCheckInRequest(recipients, message.getReminderUuid().toString());

      // Update database with last sent time
      messageRepository
          .findById(message.getId())
          .ifPresent(
              reminder -> {
                reminder.setLastReminderSent(LocalDateTime.now());
                reminder.setNextReminderDue(LocalDateTime.now().plusDays(reminder.getSpanDays()));
                reminder.setUpdatedAt(LocalDateTime.now());
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

    // Prod
    // Duration initialDelay = Duration.between(LocalDateTime.now(),
    // LocalDateTime.now().plusHours(12));

    // Dev:
    Duration initialDelay =
        Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(2));

    log.info(
        "Scheduling content message id {} to be sent in {}, if not cancelled",
        message.getId(),
        formatDuration(initialDelay),
        message.getSpanDays());

    ScheduledFuture<?> future =
        taskScheduler.schedule(
            () -> handleContentReminderAndUpdateDb(message), Instant.now().plus(initialDelay));

    activeTasks.put(createScheduleId(message.getId(), true), future);
  }

  private void handleContentReminderAndUpdateDb(MessageEntity message) {
    try {
      log.info("Handling content message schedule for message id {}", message.getId());

      // If time between now last checking is greater than 12 hours, send the content email
      MessageEntity messageOpt = messageRepository.findById(message.getId()).orElseThrow();

      LocalDateTime lastChecking = messageOpt.getLastCheckIn();
      if (Objects.isNull(lastChecking)) {
        lastChecking = LocalDateTime.now().plusYears(99);
      }
      Duration timePast = Duration.between(LocalDateTime.now(), lastChecking);

      // Prod
      // if (timePast.toHours() < 12) {

      // Dev
      if (timePast.toMinutes() < 2) {
        log.info("Skipping content message. User {} did the check in", message.getUserId());
        return;
      }

      log.info("User {} didn't check in. Sending content message.", message.getUserId());

      List<String> recipients = Arrays.asList(messageOpt.getTargets().split(";"));
      mailgunEmailService.sendHtmlContentMessage(
          recipients, messageOpt.getTitle(), messageOpt.getContent());

      // Update database disabling the current message
      messageRepository
          .findById(message.getId())
          .ifPresent(
              reminder -> {
                reminder.setUpdatedAt(LocalDateTime.now());
                reminder.setDisabledAt(LocalDateTime.now());
                messageRepository.saveAndFlush(reminder);
              });

      cancelExistingTask(message.getId(), false);
      cancelExistingTask(message.getId(), true);

    } catch (Exception e) {
      log.error("Failed to send content message for message id {}", message.getId(), e);
    }
  }

  public void cancelExistingTask(Long messageId, boolean isContent) {
    log.info("Canceling existing task: {} for content {}", messageId, isContent);
    ScheduledFuture<?> existingTask = activeTasks.remove(createScheduleId(messageId, isContent));

    if (existingTask != null) {
      boolean cancelled = existingTask.cancel(false); // false = don't interrupt if running

      if (cancelled) {
        log.info("Successfully cancelled existing scheduled task for message {}", messageId);
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

  private String createScheduleId(Long messageId, boolean isContent) {
    return messageId.toString() + (isContent ? "-check-in" : "-content");
  }
}

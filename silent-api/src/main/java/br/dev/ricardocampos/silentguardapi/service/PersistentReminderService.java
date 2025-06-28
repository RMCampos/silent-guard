package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.entity.MessageEntity;
import br.dev.ricardocampos.silentguardapi.repository.MessageRepository;
import br.dev.ricardocampos.silentguardapi.util.FormatUtil;
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

/** Service for managing persistent reminders. */
@Slf4j
@Service
@AllArgsConstructor
public class PersistentReminderService {

  private final TaskScheduler taskScheduler;

  private final MessageRepository messageRepository;

  private final MailgunEmailService mailgunEmailService;

  private static final Map<String, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();

  /**
   * Restore all active scheduled reminders on application startup. This method will fetch all
   * messages that are not disabled and schedule them for checking.
   */
  @PostConstruct
  public void restoreSchedulesOnStartup() {
    log.info("Restoring scheduled reminders, if any");
    List<MessageEntity> activeReminders = messageRepository.findByDisabledAtNull();

    for (MessageEntity message : activeReminders) {
      scheduleCheckingMessage(message);
    }

    log.info("Restored {} scheduled reminders on startup", activeReminders.size());
  }

  /**
   * Schedule a check-in message to be sent periodically based on the message's span days. This
   * method will create a new schedule if it doesn't exist, or update the existing one.
   *
   * @param message The message entity containing the details for the check-in reminder.
   */
  public void scheduleCheckingMessage(MessageEntity message) {
    // TODO: handle update or existing messages/schedules

    Duration initialDelay = Duration.between(LocalDateTime.now(), message.getNextReminderDue());
    Duration interval = Duration.ofDays(message.getSpanDays());

    if (initialDelay.isNegative()) {
      initialDelay = Duration.ZERO;
    }

    log.info(
        "Scheduling check-in message {} to be sent in {}, then repeat after {} days",
        message.getId(),
        FormatUtil.formatDuration(initialDelay),
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

      messageRepository
          .findById(message.getId())
          .ifPresent(
              reminder -> {
                reminder.setLastReminderSent(LocalDateTime.now());
                reminder.setNextReminderDue(LocalDateTime.now().plusDays(reminder.getSpanDays()));
                reminder.setUpdatedAt(LocalDateTime.now());
                messageRepository.saveAndFlush(reminder);
              });

      scheduleContentMessage(message);
    } catch (Exception e) {
      log.error("Failed to send reminder for message id {}", message.getId(), e);
    }
  }

  /**
   * Schedule the content message to be sent 12 hours after the last check-in, if the user hasn't
   * checked in again. This is a separate schedule from the check-in reminder. It will be scheduled
   * only once, after the check-in reminder is sent. If the user checks in again, this schedule will
   * be cancelled.
   *
   * @param message
   */
  public void scheduleContentMessage(MessageEntity message) {
    // TODO: handle update or existing messages/schedules

    Duration initialDelay =
        Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(12));

    log.info(
        "Scheduling content message id {} to be sent in {}, if not cancelled",
        message.getId(),
        FormatUtil.formatDuration(initialDelay),
        message.getSpanDays());

    ScheduledFuture<?> future =
        taskScheduler.schedule(
            () -> handleContentReminderAndUpdateDb(message), Instant.now().plus(initialDelay));

    activeTasks.put(createScheduleId(message.getId(), true), future);
  }

  private void handleContentReminderAndUpdateDb(MessageEntity message) {
    try {
      log.info("Handling content message schedule for message id {}", message.getId());

      MessageEntity messageOpt = messageRepository.findById(message.getId()).orElseThrow();

      LocalDateTime lastChecking = messageOpt.getLastCheckIn();
      if (Objects.isNull(lastChecking)) {
        lastChecking = LocalDateTime.now().plusYears(99);
      }

      Duration timePast = Duration.between(LocalDateTime.now(), lastChecking);

      if (timePast.toHours() < 12) {
        log.info("Skipping content message. User {} did the check in", message.getUserId());
        return;
      }

      log.info("User {} didn't check in. Sending content message.", message.getUserId());

      List<String> recipients = Arrays.asList(messageOpt.getTargets().split(";"));
      mailgunEmailService.sendHtmlContentMessage(
          recipients, messageOpt.getSubject(), messageOpt.getContent());

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

  /**
   * Cancel any existing scheduled task for the given message ID and type (check-in or content).
   * This is useful to prevent duplicate tasks from running if the user checks in again.
   *
   * @param messageId The ID of the message to cancel the task for.
   * @param isContent True if the task is for content, false if it's for check-in.
   */
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

  private String createScheduleId(Long messageId, boolean isContent) {
    return messageId.toString() + (isContent ? "-check-in" : "-content");
  }
}

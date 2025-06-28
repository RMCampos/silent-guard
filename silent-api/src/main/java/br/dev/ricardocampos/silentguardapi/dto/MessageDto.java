package br.dev.ricardocampos.silentguardapi.dto;

import br.dev.ricardocampos.silentguardapi.entity.MessageEntity;
import br.dev.ricardocampos.silentguardapi.util.FormatUtil;
import io.jsonwebtoken.lang.Arrays;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing a message. This class is used to encapsulate the
 * properties of a message, including its ID, subject, recipients, content, and other related
 * information.
 *
 * @param id the unique identifier of the message
 * @param subject the subject of the message
 * @param recipients the list of email addresses of the message recipients
 * @param content the content of the message
 * @param daysToTrigger the number of days after which the message should be triggered
 * @param active indicates whether the message is active or not
 * @param lastCheckIn the last time the message was checked in a human-readable format
 * @param nextReminder the next reminder duration in a human-readable format
 */
public record MessageDto(
    Long id,
    @NotNull String subject,
    @NotEmpty(message = "At least one recipient is required") @Valid List<@Email String> recipients,
    @NotNull String content,
    @NotNull Integer daysToTrigger,
    Boolean active,
    String lastCheckIn,
    String nextReminder) {

  /**
   * Converts a MessageEntity to a MessageDto.
   *
   * @param e the MessageEntity to convert
   * @return a MessageDto representing the given MessageEntity
   */
  public static MessageDto fromEntity(MessageEntity e) {
    Boolean active = Objects.isNull(e.getDisabledAt());
    List<String> emails = Arrays.asList(e.getTargets().split(";"));
    Duration durationNext = Duration.between(LocalDateTime.now(), e.getNextReminderDue());
    return new MessageDto(
        e.getId(),
        e.getSubject(),
        emails,
        e.getContent(),
        e.getSpanDays(),
        active,
        FormatUtil.formatTimeAgo(e.getLastCheckIn()),
        FormatUtil.formatDuration(durationNext));
  }
}

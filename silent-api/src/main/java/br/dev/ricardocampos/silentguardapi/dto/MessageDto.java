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

public record MessageDto(
    Long id,
    @NotNull String subject,
    @NotEmpty(message = "At least one recipient is required") @Valid List<@Email String> recipients,
    @NotNull String content,
    @NotNull Integer daysToTrigger,
    Boolean active,
    String lastCheckIn,
    String nextReminder) {

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

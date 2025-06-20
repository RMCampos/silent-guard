package br.dev.ricardocampos.silentguardapi.dto;

import br.dev.ricardocampos.silentguardapi.entity.MessageEntity;
import java.util.Objects;

public record MessageDto(
    Long id,
    String title,
    String recipient,
    String content,
    Integer daysToTrigger,
    Boolean active) {

  public static MessageDto fromEntity(MessageEntity e) {
    Boolean active = Objects.isNull(e.getDisabledAt());
    return new MessageDto(
        e.getId(), e.getTitle(), e.getTargets(), e.getContent(), e.getSpanDays(), active);
  }
}

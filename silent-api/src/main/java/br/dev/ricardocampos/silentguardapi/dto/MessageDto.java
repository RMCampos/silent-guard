package br.dev.ricardocampos.silentguardapi.dto;

import br.dev.ricardocampos.silentguardapi.entity.MessageEntity;

public record MessageDto(
    Long id, String title, String recipient, String content, Integer daysToTrigger) {

  public static MessageDto fromEntity(MessageEntity e) {
    return new MessageDto(e.getId(), e.getTitle(), e.getTargets(), e.getContent(), e.getSpanDays());
  }
}

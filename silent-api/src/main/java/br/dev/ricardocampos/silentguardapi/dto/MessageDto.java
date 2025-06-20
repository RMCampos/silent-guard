package br.dev.ricardocampos.silentguardapi.dto;

import br.dev.ricardocampos.silentguardapi.entity.MessageEntity;
import io.jsonwebtoken.lang.Arrays;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public record MessageDto(
    Long id,
    @NotNull String title,
    @NotEmpty(message = "At least one recipient is required") @Valid
        List<@Email(message = "Invalid email format") String> recipients,
    @NotNull String content,
    @NotNull Integer daysToTrigger,
    Boolean active) {

  public static MessageDto fromEntity(MessageEntity e) {
    Boolean active = Objects.isNull(e.getDisabledAt());
    List<String> emails = Arrays.asList(e.getTargets().split(";"));
    return new MessageDto(e.getId(), e.getTitle(), emails, e.getContent(), e.getSpanDays(), active);
  }
}

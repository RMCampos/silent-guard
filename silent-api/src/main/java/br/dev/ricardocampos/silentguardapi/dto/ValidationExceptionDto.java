package br.dev.ricardocampos.silentguardapi.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class ValidationExceptionDto {

  private static final String MESSAGE_TEMPLATE = "%d field(s) with validation problems!";

  private final String errorMessage;

  private final List<FieldIssueDto> fields;

  public ValidationExceptionDto(List<FieldError> errors) {
    this.fields =
        errors.stream()
            .map(error -> new FieldIssueDto(error.getField(), error.getDefaultMessage()))
            .toList();
    this.errorMessage = String.format(MESSAGE_TEMPLATE, fields.size());
  }
}

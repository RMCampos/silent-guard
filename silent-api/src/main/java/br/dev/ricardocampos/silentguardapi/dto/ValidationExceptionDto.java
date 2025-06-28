package br.dev.ricardocampos.silentguardapi.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.validation.FieldError;

/**
 * Data Transfer Object (DTO) representing validation errors in a request. This class encapsulates
 * the error message and a list of field issues that occurred during validation.
 */
@Getter
public class ValidationExceptionDto {

  private static final String MESSAGE_TEMPLATE = "%d field(s) with validation problems!";

  private final String errorMessage;

  private final List<FieldIssueDto> fields;

  /**
   * Constructor for ValidationExceptionDto that initializes the error message and fields based on
   * the provided list of FieldError objects.
   *
   * @param errors the list of FieldError objects representing the validation errors
   */
  public ValidationExceptionDto(List<FieldError> errors) {
    this.fields =
        errors.stream()
            .map(error -> new FieldIssueDto(error.getField(), error.getDefaultMessage()))
            .toList();
    this.errorMessage = String.format(MESSAGE_TEMPLATE, fields.size());
  }
}

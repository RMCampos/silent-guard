package br.dev.ricardocampos.silentguardapi.controller;

import br.dev.ricardocampos.silentguardapi.dto.ValidationExceptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** This class provides exceptions handling for requests. */
@RestControllerAdvice
public class RestExceptionController {

  /**
   * Handles MethodArgumentNotValidException and returns a ResponseEntity with a
   * ValidationExceptionDto.
   *
   * @param ex the MethodArgumentNotValidException to handle
   * @return a ResponseEntity containing the validation errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ValidationExceptionDto> handleValidationException(
      MethodArgumentNotValidException ex) {
    return ResponseEntity.badRequest().body(new ValidationExceptionDto(ex.getFieldErrors()));
  }
}

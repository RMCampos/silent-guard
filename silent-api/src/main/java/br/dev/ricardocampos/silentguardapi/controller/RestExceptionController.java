package br.dev.ricardocampos.silentguardapi.controller;

import br.dev.ricardocampos.silentguardapi.dto.ValidationExceptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** This class provides exceptions handling for requests. */
@RestControllerAdvice
public class RestExceptionController {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ValidationExceptionDto> handleValidationException(
      MethodArgumentNotValidException ex) {
    return ResponseEntity.badRequest().body(new ValidationExceptionDto(ex.getFieldErrors()));
  }
}

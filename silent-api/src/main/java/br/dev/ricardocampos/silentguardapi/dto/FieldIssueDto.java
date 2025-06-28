package br.dev.ricardocampos.silentguardapi.dto;

/**
 * Data Transfer Object (DTO) representing an issue with a specific field. This class is used to
 * encapsulate the field name and the associated message for validation or error reporting purposes.
 *
 * @param fieldName the name of the field that has an issue
 * @param fieldMessage the message describing the issue with the field
 */
public record FieldIssueDto(String fieldName, String fieldMessage) {}

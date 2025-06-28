package br.dev.ricardocampos.silentguardapi.dto;

/**
 * Data Transfer Object (DTO) representing user information. This class is used to encapsulate the
 * properties of a user, including their ID, nickname, name, and other related information.
 *
 * @param sub the unique identifier of the user
 * @param nickname the nickname of the user
 * @param name the name of the user
 */
public record UserInfoDto(
    String sub,
    String nickname,
    String name,
    String picture,
    String updated_at,
    String email,
    Boolean email_verified) {}

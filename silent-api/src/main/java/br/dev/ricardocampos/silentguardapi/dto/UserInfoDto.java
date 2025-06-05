package br.dev.ricardocampos.silentguardapi.dto;

public record UserInfoDto(
    String sub,
    String nickname,
    String name,
    String picture,
    String updated_at,
    String email,
    Boolean email_verified) {}

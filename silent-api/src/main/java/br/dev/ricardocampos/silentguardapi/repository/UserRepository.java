package br.dev.ricardocampos.silentguardapi.repository;

import br.dev.ricardocampos.silentguardapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {}

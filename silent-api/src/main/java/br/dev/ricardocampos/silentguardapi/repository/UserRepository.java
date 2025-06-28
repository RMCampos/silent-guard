package br.dev.ricardocampos.silentguardapi.repository;

import br.dev.ricardocampos.silentguardapi.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing UserEntity objects in the database. This interface extends
 * JpaRepository to provide CRUD operations and custom query methods.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByEmail(String email);
}

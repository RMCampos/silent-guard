package br.dev.ricardocampos.silentguardapi.repository;

import br.dev.ricardocampos.silentguardapi.entity.MessageEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

  List<MessageEntity> findAllByUserId(Long userId);

  List<MessageEntity> findAllByIdIn(List<Long> idList);

  List<MessageEntity> findByDisabledAtNull();

  Optional<MessageEntity> findByReminderUuid(UUID uuid);
}

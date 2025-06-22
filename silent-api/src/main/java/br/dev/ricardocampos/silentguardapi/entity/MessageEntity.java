package br.dev.ricardocampos.silentguardapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table(name = "sg_messages")
public class MessageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "span_days", nullable = false)
  private Integer spanDays;

  @Column(nullable = false, length = 300)
  private String title;

  @Column(nullable = false, length = 3000)
  private String targets;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "last_reminder_sent")
  private LocalDateTime lastReminderSent;

  @Column(name = "next_reminder_due", nullable = false)
  private LocalDateTime nextReminderDue;

  @Column(name = "last_check_in")
  private LocalDateTime lastCheckIn;

  @Column(name = "reminder_uuid", columnDefinition = "uuid", nullable = false, unique = true)
  private UUID reminderUuid;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "disabled_at")
  private LocalDateTime disabledAt;
}

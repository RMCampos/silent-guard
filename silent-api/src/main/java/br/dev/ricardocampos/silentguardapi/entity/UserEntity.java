package br.dev.ricardocampos.silentguardapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Entity representing a user in the Silent Guard application. This entity stores user information
 * such as email, last check-in time, break days, and timestamps for creation, update, and
 * disabling.
 */
@Data
@Entity
@Table(name = "sg_users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(name = "last_check_in", nullable = false)
  private LocalDateTime lastCheckIn;

  @Column(name = "break_days")
  private Integer breakDays;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "disabled_at")
  private LocalDateTime disabledAt;
}

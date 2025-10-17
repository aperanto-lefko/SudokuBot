package ru.sudoku.game.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true, nullable = false)
    Long chatId;              // уникальный идентификатор Telegram чата
    @Column(unique = true)
    String username;          // никнейм Telegram (может быть null)
    boolean consentGiven;     // пользователь дал согласие
    @Column(nullable = false)
    LocalDateTime consentDate; //дата подтверждения
    @Column(nullable = false)
    LocalDateTime createdAt; //дата создания
}

package ru.sudoku.game.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "game_results")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "difficulty")
    private String difficulty; //уровень сложности

    @Column(name = "started_at")
    private LocalDateTime startedAt; // Время начала игры

    @Column(name = "completed_at")
    private LocalDateTime completedAt; // Время завершения

    @Column(name = "completion_time")
    private Long completionTime; // Продолжительность в секундах (вычисляемое поле)

    @Column(name = "score")
    private Integer score; //количество баллов за игру

    @Column(name = "mistakes_count")
    private Integer mistakesCount; //число неудачных попытокщддддддщщщщщщщщщщхзж
}

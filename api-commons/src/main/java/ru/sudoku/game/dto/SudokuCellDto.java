package ru.sudoku.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SudokuCellDto {
    private int value;      // текущее значение клетки
    private boolean fixed;
}

package ru.sudoku.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SudokuCell {
    private int value;      // текущее значение клетки
    private boolean fixed;  // true — число из генератора, false — пустая или пользовательская
}

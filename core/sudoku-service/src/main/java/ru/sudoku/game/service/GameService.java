package ru.sudoku.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sudoku.game.generator.SudokuGenerator;
import ru.sudoku.game.model.SudokuCell;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GameService {

    private final SudokuGenerator generator;
    private final ConcurrentHashMap<Long, SudokuCell[][]> games = new ConcurrentHashMap<>();

    public SudokuCell[][] newGame(long chatId) {
        int[][] board = generator.generate(3);
        SudokuCell[][] cells = new SudokuCell[4][4];

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int value = board[r][c];
                cells[r][c] = new SudokuCell(value, value != 0); // fixed = true, если число сгенерировано
            }
        }

        games.put(chatId, cells);
        return cells;
    }
    public SudokuCell[][] newGame(long chatId, int blanks) {
        int[][] board = generator.generate(blanks);
        SudokuCell[][] cells = new SudokuCell[4][4];

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int value = board[r][c];
                cells[r][c] = new SudokuCell(value, value != 0);
            }
        }

        games.put(chatId, cells);
        return cells;
    }

    public SudokuCell[][] getBoard(long chatId) {
        return games.get(chatId);
    }

    public void setCell(long chatId, int row, int col, int value) {
        SudokuCell[][] board = games.get(chatId);
        if (board != null && !board[row][col].isFixed()) {
            board[row][col].setValue(value);
        }

    }
    public boolean isSolved(long chatId) {
        SudokuCell[][] board = games.get(chatId);
        if (board == null) return false;

        int size = 4;
        int blockSize = 2;

        // Проверка строк и столбцов
        for (int i = 0; i < size; i++) {
            // Массивы для отслеживания, какие числа уже встречались в строке и столбце
            boolean[] rowCheck = new boolean[size + 1];
            boolean[] colCheck = new boolean[size + 1];

            for (int j = 0; j < size; j++) {
                int rowVal = board[i][j].getValue();
                int colVal = board[j][i].getValue();
                // Проверка, что число в допустимом диапазоне 1..size
                if ((rowVal < 1) || (rowVal > size) || (colVal < 1) || (colVal > size)) {
                    return false;
                }
                // Проверяем, встречалось ли это число ранее в строке или столбце
                if (rowCheck[rowVal]) { // число уже встречалось в строке
                    return false;
                }
                if (colCheck[colVal]) { // число уже встречалось в столбце
                    return false;
                }
// Отмечаем число как встреченное
                rowCheck[rowVal] = true;
                colCheck[colVal] = true;
            }
        }

        // Проверка блоков 2x2
        for (int blockRow = 0; blockRow < size; blockRow += blockSize) {
            for (int blockCol = 0; blockCol < size; blockCol += blockSize) {
                boolean[] blockCheck = new boolean[size + 1]; // массив для отслеживания чисел в блоке
                // Проходим по каждой ячейке блока 2x2
                for (int r = 0; r < blockSize; r++) {
                    for (int c = 0; c < blockSize; c++) {
                        int val = board[blockRow + r][blockCol + c].getValue();
                        // Если число уже встречалось в блоке — доска неверна
                        if (blockCheck[val]) {
                            return false;
                        }
                        // Отмечаем число как встреченное
                        blockCheck[val] = true;
                    }
                }
            }
        }
// Если все проверки прошли успешно — доска заполнена правильно
        return true;
    }
//Проверка все ли поля заполнены
    public boolean isBoardFull(long chatId) {
        SudokuCell[][] board = games.get(chatId);
        if (board == null) return false;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c].getValue() == 0) {
                    return false; // есть пустая клетка
                }
            }
        }
        return true; // все клетки заполнены
    }

}

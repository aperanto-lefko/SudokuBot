package ru.sudoku.game.generator;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SudokuGenerator {
    private final Random random = new Random();

    public int[][] generate(int blanks) {
        int[][] board = new int[4][4]; // создаём пустое поле 4х4
        fillboard(board); // заполняем всё поле корректными числами (от 1 до 4)
        removeCells(board, blanks);
        return board;
    }

    // Рекурсивная функция для заполнения доски числами 1–9 по правилам судоку
    private boolean fillboard(int[][] board) {
        // проходим по всем клеткам
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                // если клетка пустая (0), пытаемся поставить туда число
                if (board[row][col] == 0) {
                    int[] nums = randPermutation(); // создаём случайный порядок чисел 1–9
                    for (int n : nums) { // пробуем поставить каждое число
                        if (isSafe(board, row, col, n)) { // проверяем, не нарушит ли это число правила судоку
                            board[row][col] = n;   // ставим число
                            if (fillboard(board)) {  // рекурсивно заполняем дальше
                               return true;  // если доска заполнилась успешно — выходим
                            }
                            board[row][col] = 0;  // если не получилось — откатываем (backtracking)
                        }
                    }
                    // если ни одно число не подошло — откат назад
                    return false;
                }
            }
        }
        // если дошли до конца без пустых клеток — всё заполнено
        return true;
    }

    // Проверка, можно ли поставить число num в позицию (row, col)
    private boolean isSafe(int[][] board, int row, int col, int num) {
        // Проверка строки и столбца
        for (int i = 0; i < 4; i++) {
            if (board[row][i] == num || board[i][col] == num) return false;
        }

        // Проверка блока 2x2
        int startRow = row - row % 2;
        int startCol = col - col % 2;
        for (int r = startRow; r < startRow + 2; r++) {
            for (int c = startCol; c < startCol + 2; c++) {
                if (board[r][c] == num) return false;
            }
        }

        return true;
    }

    // Удаляем случайные клетки, чтобы сделать головоломку (оставить blanks пустых клеток)
    private void removeCells(int[][] board, int blanks) {
        int removed = 0; // счётчик удалённых клеток
        while (removed < blanks) {
            int r = random.nextInt(4); // случайная строка
            int c = random.nextInt(4); // случайный столбец
            if (board[r][c] != 0) { // если клетка не пуста
                board[r][c] = 0; // очищаем её (ставим 0)
                removed++;
            }
        }

    }

    // Генерация случайного порядка чисел 1–9 (перестановка)
    private int[] randPermutation() {
        int[] nums = {1, 2, 3, 4};// исходный массив чисел
        for (int i = nums.length - 1; i > 0; i--) { // алгоритм Фишера-Йетса (перемешивание)
            int j = random.nextInt(i + 1); // выбираем случайный индекс от 0 до i
            int tmp = nums[i];
            nums[i] = nums[j];
            nums[j] = tmp;
        }
        return nums;
    }
}

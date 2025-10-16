package ru.sudoku.game.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.sudoku.game.dto.SudokuCellDto;
import ru.sudoku.game.exception.BoardNotFoundException;
import ru.sudoku.game.generator.SudokuGenerator;
import ru.sudoku.game.mapper.SudokuCellMapper;
import ru.sudoku.game.model.SudokuCell;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameService {

    SudokuGenerator generator;
    ConcurrentHashMap<Long, SudokuCell[][]> games = new ConcurrentHashMap<>();
    SudokuCellMapper sudokuCellMapper;

    /**
     * Создаёт новую игру Судоку для заданного пользователя (chatId) с указанным количеством пустых ячеек.
     *
     * <p>Метод генерирует новую 4x4 доску Судоку с помощью {@link SudokuGenerator}, где
     * число пустых ячеек задаётся параметром {@code blanks}. Затем доска преобразуется в массив
     * объектов {@link SudokuCell}, где каждый объект содержит значение ячейки и флаг {@code fixed},
     * который равен {@code true}, если число сгенерировано, и {@code false}, если ячейка пустая.</p>
     *
     * <p>Созданная доска может быть сохранена в коллекции игр по идентификатору {@code chatId}
     * для отслеживания состояния конкретного пользователя.</p>
     *
     * @param chatId уникальный идентификатор пользователя, для которого создаётся игра
     * @param blanks количество пустых ячеек, которые должны быть оставлены при генерации доски
     * @return двумерный массив {@link SudokuCell} размером 4x4, представляющий новую игру
     */
    public SudokuCellDto[][] newGame(long chatId, int blanks) {
        int[][] board = generator.generate(blanks);
        SudokuCell[][] cells = new SudokuCell[4][4];

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int value = board[r][c];
                cells[r][c] = new SudokuCell(value, value != 0);
            }
        }
        games.put(chatId, cells);
        return sudokuCellMapper.toDto(cells);
    }
    /**
     * Возвращает текущую доску Судоку для пользователя с заданным идентификатором.
     *
     * @param chatId уникальный идентификатор пользователя
     * @return двумерный массив {@link SudokuCell} размером 4x4, представляющий текущее состояние игры,
     *         или {@code null}, если игра для данного пользователя не существует
     */
    public SudokuCellDto[][] getBoard(long chatId) {
        SudokuCell[][] board = games.get(chatId);
        if (board == null) {
            throw new BoardNotFoundException(chatId);
        }
        return sudokuCellMapper.toDto(board);
    }
    /**
     * Устанавливает значение ячейки на доске Судоку для конкретного пользователя,
     * если эта ячейка не является фиксированной (сгенерированной автоматически).
     *
     * @param chatId уникальный идентификатор пользователя
     * @param row индекс строки (0–3) ячейки, которую нужно изменить
     * @param col индекс столбца (0–3) ячейки, которую нужно изменить
     * @param value новое значение для ячейки
     *
     * <p>Если доска для пользователя не существует или выбранная ячейка фиксирована,
     * метод ничего не делает.</p>
     */
    public void setCell(long chatId, int row, int col, int value) {
        SudokuCell[][] board = games.get(chatId);
        if (board != null && !board[row][col].isFixed()) {
            board[row][col].setValue(value);
        }
    }
    /**
     * Проверяет, решена ли доска Судоку для конкретного пользователя.
     *
     * <p>Метод проверяет текущую доску 4x4 по трём правилам:
     * <ul>
     *   <li>Каждая строка содержит числа 1–4 без повторений.</li>
     *   <li>Каждый столбец содержит числа 1–4 без повторений.</li>
     *   <li>Каждый блок 2x2 содержит числа 1–4 без повторений.</li>
     * </ul>
     * Если доска не существует или хотя бы одно правило нарушено, метод возвращает {@code false}.</p>
     *
     * @param chatId уникальный идентификатор пользователя
     * @return {@code true}, если доска полностью и корректно решена, {@code false} в противном случае
     */
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
    /**
     * Проверяет, полностью ли заполнена доска Судоку для конкретного пользователя.
     *
     * <p>Метод проходит по всем ячейкам доски 4x4 и возвращает {@code false},
     * если хотя бы одна ячейка пуста (значение равно 0). Если все ячейки
     * содержат числа, возвращает {@code true}.</p>
     *
     * @param chatId уникальный идентификатор пользователя
     * @return {@code true}, если все ячейки доски заполнены, {@code false} если есть пустые ячейки
     */
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

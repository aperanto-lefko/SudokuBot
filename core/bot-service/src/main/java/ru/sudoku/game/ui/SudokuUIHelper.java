package ru.sudoku.game.ui;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sudoku.game.dto.SudokuCellDto;
import ru.sudoku.game.model.SudokuCell;

import java.util.ArrayList;
import java.util.List;

@Component
public class SudokuUIHelper {
    /**
     * Создает интерактивное сообщение с игровым полем Sudoku для Telegram бота
     *
     * @param chatId идентификатор чата для отправки сообщения
     * @param board двумерный массив ячеек игрового поля Sudoku
     * @return готовое сообщение SendMessage с inline-клавиатурой игрового поля
     *
     * @apiNote Метод преобразует игровое поле в интерактивную клавиатуру, где:
     *          - Фиксированные ячейки отображаются как обычные числа без возможности взаимодействия
     *          - Изменяемые ячейки отображаются с точкой после числа или "❓" для пустых ячеек
     *          - Каждая изменяемая ячейка является кнопкой с callback данными в формате "CELL_X_Y"
     */
    public SendMessage buildBoardMessage(long chatId, SudokuCellDto[][] board) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int size = board.length;
        for (int r = 0; r < size; r++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int c = 0; c < size; c++) {
                SudokuCellDto cell = board[r][c];
                InlineKeyboardButton btn = new InlineKeyboardButton();

                if (cell.isFixed()) {
                    // фиксированные числа: замок, не интерактивные
                    btn.setText(cell.getValue() + "");
                    btn.setCallbackData("LOCKED");
                } else {
                    // пустая клетка или пользовательское число: интерактивные
                    btn.setText(cell.getValue() == 0 ? "❓" : String.valueOf(cell.getValue() + "."));
                    btn.setCallbackData("CELL_" + r + "_" + c);
                }

                row.add(btn);

            }
            rows.add(row);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText("Ваше Sudoku:");
        msg.setReplyMarkup(markup);
        return msg;
    }
    /**
     * Создает сообщение с клавиатурой для выбора числа в указанной клетке Sudoku
     *
     * @param chatId идентификатор чата для отправки сообщения
     * @param row номер строки выбранной клетки (0-based)
     * @param col номер столбца выбранной клетки (0-based)
     * @return готовое сообщение SendMessage с клавиатурой выбора чисел
     *
     * @apiNote Метод создает клавиатуру с числами от 1 до 4 и управляющими кнопками:
     *          - Числа 1-4: кнопки с callback данными в формате "VALUE_row_col_number"
     *          - Кнопка "🧹 стереть": устанавливает значение 0 для клетки
     *          - Кнопка "↩️ отмена": возвращает к предыдущему действию
     *          В тексте сообщения координаты отображаются в user-friendly формате (1-based)
     */

    public SendMessage buildNumberSelection(long chatId, int row, int col) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // одна строка со всеми кнопками: цифры 1-4, стереть, отмена
        List<InlineKeyboardButton> singleRow = new ArrayList<>();

        // цифры 1-4
        for (int num = 1; num <= 4; num++) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(String.valueOf(num));
            btn.setCallbackData("VALUE_" + row + "_" + col + "_" + num);
            singleRow.add(btn);
        }
        // стереть
        InlineKeyboardButton erase = new InlineKeyboardButton();
        erase.setText("🧹");
        erase.setCallbackData("VALUE_" + row + "_" + col + "_0");
        singleRow.add(erase);

        // отмена
        InlineKeyboardButton cancel = new InlineKeyboardButton();
        cancel.setText("↩️");
        cancel.setCallbackData("CANCEL");
        singleRow.add(cancel);

        rows.add(singleRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);

        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText("Выберите число для клетки [" + (row + 1) + "," + (col + 1) + "]");
        msg.setReplyMarkup(markup);
        return msg;
    }
    /**
     * Создает сообщение с выбором уровня сложности для новой игры в Sudoku
     *
     * @param chatId идентификатор чата для отправки сообщения
     * @return готовое сообщение SendMessage с клавиатурой выбора сложности
     *
     * @apiNote Метод предоставляет три уровня сложности:
     *          -  Легкий (DIFFICULTY_EASY)
     *          -  Средний (DIFFICULTY_MEDIUM)
     *          -  Сложный (DIFFICULTY_HARD)
     *          Все кнопки расположены в одной строке для компактного отображения
     */
    public SendMessage buildDifficultySelection(long chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> line1 = new ArrayList<>();
        line1.add(button("🟢 Легкий", "DIFFICULTY_EASY"));
        line1.add(button("🟡 Средний", "DIFFICULTY_MEDIUM"));
        line1.add(button("🔴 Сложный", "DIFFICULTY_HARD"));
        rows.add(line1);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);

        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText("Выберите уровень сложности Sudoku:");
        msg.setReplyMarkup(markup);
        return msg;
    }
    /**
     * Вспомогательный метод для создания inline-кнопки с заданным текстом и callback данными
     *
     * @param text отображаемый текст на кнопке
     * @param callback данные для callback при нажатии на кнопку
     * @return настроенный объект InlineKeyboardButton
     *
     * @apiNote Упрощает создание кнопок, сокращая дублирование кода в методах построения клавиатур
     */
    private InlineKeyboardButton button(String text, String callback) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(callback);
        return btn;
    }

    /**
     * Создает сообщение с кнопкой "Старт" которая отправляет команду /start
     */
    public SendMessage buildStartButtonMessage(long chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> startRow = new ArrayList<>();
        InlineKeyboardButton startButton = new InlineKeyboardButton();
        startButton.setText("🎮 Начать игру");
        startButton.setSwitchInlineQueryCurrentChat("/start");
        startRow.add(startButton);
        rows.add(startRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);

        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
               msg.setReplyMarkup(markup);
        return msg;
    }
}

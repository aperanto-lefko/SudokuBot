package ru.sudoku.game.ui;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sudoku.game.model.SudokuCell;

import java.util.ArrayList;
import java.util.List;

@Component
public class SudokuUIHelper {

    public SendMessage buildBoardMessage(long chatId, SudokuCell[][] board) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int size = board.length;
        for (int r = 0; r < size; r++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int c = 0; c < size; c++) {
                SudokuCell cell = board[r][c];
                InlineKeyboardButton btn = new InlineKeyboardButton();

                if (cell.isFixed()) {
                    // фиксированные числа: замок, не интерактивные
                    btn.setText(cell.getValue() + "");
                    btn.setCallbackData("LOCKED");
                } else {
                    // пустая клетка или пользовательское число: интерактивные
                    btn.setText(cell.getValue() == 0 ? "❓" : String.valueOf(cell.getValue() + ".1"));
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

    public SendMessage buildNumberSelection(long chatId, int row, int col) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // цифры 1–9
        for (int i = 1; i <= 4; i += 2) {
            List<InlineKeyboardButton> line = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                int num = i + j;
                if (num > 4) {
                    break;
                }
                InlineKeyboardButton btn = new InlineKeyboardButton();
                btn.setText(String.valueOf(num));
                btn.setCallbackData("VALUE_" + row + "_" + col + "_" + num);
                line.add(btn);
            }
            rows.add(line);
        }

        // последняя строка: стереть / отмена
        List<InlineKeyboardButton> controlRow = new ArrayList<>();
        InlineKeyboardButton erase = new InlineKeyboardButton();
        erase.setText("🧹 стереть");
        erase.setCallbackData("VALUE_" + row + "_" + col + "_0");

        InlineKeyboardButton cancel = new InlineKeyboardButton();
        cancel.setText("↩️ отмена");
        cancel.setCallbackData("CANCEL");

        controlRow.add(erase);
        controlRow.add(cancel);
        rows.add(controlRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);

        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText("Выберите число для клетки [" + (row + 1) + "," + (col + 1) + "]");
        msg.setReplyMarkup(markup);
        return msg;
    }
}

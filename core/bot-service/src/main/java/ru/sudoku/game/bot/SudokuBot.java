package ru.sudoku.game.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sudoku.game.model.SudokuCell;
import ru.sudoku.game.service.GameService;
import ru.sudoku.game.ui.SudokuUIHelper;

@Component
@RequiredArgsConstructor
public class SudokuBot extends TelegramLongPollingBot {
    private final GameService gameService;
    private final SudokuUIHelper uiHelper;
    @Value("${telegram.bot.username}")
    private String botUserName;
    private final String botToken = System.getenv("TELEGRAM_SUDOKU_BOT_TOKEN");

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
            return;
        }
        //  Пользователь отправил сообщение (например, /start)
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            if ("/start".equalsIgnoreCase(text)) {
                SendMessage msg = uiHelper.buildDifficultySelection(chatId);
//                SudokuCell[][] board = gameService.newGame(chatId);
//                SendMessage msg = uiHelper.buildBoardMessage(chatId, board);
                executeSafe(msg);
            } else {
                executeSafe(SendMessage.builder()
                        .chatId(chatId)
                        .text("Напиши /start чтобы начать новую игру")
                        .build());
            }
        }
    }


    private void executeSafe(SendMessage msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleCallback(CallbackQuery query) {
        String data = query.getData();
        long chatId = query.getMessage().getChatId();
        if (data.startsWith("DIFFICULTY_")) {
            String level = data.substring("DIFFICULTY_".length());
            int blanks;

            switch (level) {
                case "EASY":
                    blanks = 3;
                    break;
                case "MEDIUM":
                    blanks = 6;
                    break;
                case "HARD":
                    blanks = 8;
                    break;
                default:
                    blanks = 4;
            }

            SudokuCell[][] board = gameService.newGame(chatId, blanks);
            SendMessage msg = uiHelper.buildBoardMessage(chatId, board);
            executeSafe(msg);
        } else if (data.startsWith("CELL_")) {
            String[] parts = data.split("_");
            int r = Integer.parseInt(parts[1]);
            int c = Integer.parseInt(parts[2]);

            SendMessage msg = uiHelper.buildNumberSelection(chatId, r, c);
            executeSafe(msg);
        } else if (data.startsWith("VALUE_")) {
            String[] parts = data.split("_");
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            int value = Integer.parseInt(parts[3]);

            gameService.setCell(chatId, row, col, value);
            SudokuCell[][] board = gameService.getBoard(chatId);

            SendMessage msg = uiHelper.buildBoardMessage(chatId, board);
            executeSafe(msg);
            if (gameService.isBoardFull(chatId)) { // метод проверяет есть ли нули
                if (gameService.isSolved(chatId)) {
                    executeSafe(SendMessage.builder()
                            .chatId(chatId)
                            .text("Поздравляем! Sudoku решено правильно 🎉")
                            .build());
                } else {
                    executeSafe(SendMessage.builder()
                            .chatId(chatId)
                            .text("Все клетки заполнены, но решение неверное ❌")
                            .build());
                }
            }
        } else if ("CANCEL".equals(data)) {
            SudokuCell[][] board = gameService.getBoard(chatId);
            SendMessage msg = uiHelper.buildBoardMessage(chatId, board);
            executeSafe(msg);
        }
    }


    @Override
    public String getBotUsername() {
        return botUserName;
    }

    // Возвращает токен (секретный ключ доступа)
    @Override
    public String getBotToken() {
        return botToken;
    }
}

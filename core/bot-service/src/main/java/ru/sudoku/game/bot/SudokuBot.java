package ru.sudoku.game.bot;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sudoku.game.dto.SudokuCellDto;
import ru.sudoku.game.feign.GameServiceClient;
import ru.sudoku.game.ui.SudokuUIHelper;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SudokuBot extends TelegramLongPollingBot {
    final GameServiceClient gameServiceclient;
    final SudokuUIHelper uiHelper;
    @Value("${telegram.bot.username}")
    String botUserName;
    @Value("${telegram.bot.token}")
    String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        log.info(" Получено новое обновление от Telegram");
        if (update.hasCallbackQuery()) {
            log.info("Обработка нажатия кнопки пользователем");
            handleCallback(update.getCallbackQuery());
            return;
        }
        //  Пользователь отправил сообщение (например, /start)
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            log.info("Пользователь {} отправил сообщение: {}", chatId, text);
            if ("/start".equalsIgnoreCase(text)) {
                log.info("Начало новой игры для пользователя {}", chatId);
                SendMessage msg = uiHelper.buildDifficultySelection(chatId);
                executeSafe(msg);
            } else if ("/rules".equalsIgnoreCase(text)) {
                log.info("Пользователь {} запросил правила игры", chatId);
                String rulesText = getRules();
                sendText(chatId, rulesText);
            } else {
                log.warn("Неизвестная команда от пользователя {}", chatId);
                sendText(chatId, "Напиши /start чтобы начать новую игру\n/rules- показать правила игры");
            }
        }
    }


    private void executeSafe(SendMessage msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения в Telegram: {}", e.getMessage(), e);
        }
    }

    private void handleCallback(CallbackQuery query) {
        String data = query.getData();
        long chatId = query.getMessage().getChatId();
        log.info("Обработка callback-запроса '{}' от пользователя {}", data, chatId);
        try {
            if (data.startsWith("DIFFICULTY_")) {
                String level = data.substring("DIFFICULTY_".length());
                log.info("Пользователь {} выбрал уровень сложности: {}", chatId, level);
                int blanks;
                switch (level) {
                    case "EASY" -> blanks = 3;
                    case "MEDIUM" -> blanks = 6;
                    case "HARD" -> blanks = 8;
                    default -> blanks = 4;
                }

                SudokuCellDto[][] board = gameServiceclient.newGame(chatId, blanks);
                log.info("Новая игра создана для пользователя {}", chatId);
                SendMessage msg = uiHelper.buildBoardMessage(chatId, board);
                executeSafe(msg);
            } else if (data.startsWith("CELL_")) {
                String[] parts = data.split("_");
                int r = Integer.parseInt(parts[1]);
                int c = Integer.parseInt(parts[2]);
                log.info(" Пользователь {} выбрал клетку ({}, {})", chatId, r, c);
                SendMessage msg = uiHelper.buildNumberSelection(chatId, r, c);
                executeSafe(msg);
            } else if (data.startsWith("VALUE_")) {
                String[] parts = data.split("_");
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                int value = Integer.parseInt(parts[3]);
                log.info(" Пользователь {} устанавливает значение {} в клетку ({}, {})", chatId, value, row, col);
                gameServiceclient.setCell(chatId, row, col, value);
                SudokuCellDto[][] board = gameServiceclient.getBoard(chatId);

                SendMessage msg = uiHelper.buildBoardMessage(chatId, board);
                executeSafe(msg);
                if (gameServiceclient.isBoardFull(chatId)) { // метод проверяет есть ли нули
                    log.info(" Проверка заполненности поля у пользователя {}", chatId);
                    if (gameServiceclient.isSolved(chatId)) {
                        log.info("Пользователь {} успешно решил судоку!", chatId);
                        sendText(chatId, "Поздравляем! Sudoku решено правильно 🎉");
                        SendMessage startButtonMessage = uiHelper.buildStartButtonMessage(chatId);
                        executeSafe(startButtonMessage);
                    } else {
                        log.info(" Пользователь {} заполнил судоку неверно", chatId);
                        sendText(chatId, "Все клетки заполнены, но решение неверное ❌");
                    }
                }
            } else if ("CANCEL".equals(data)) {
                log.info("↩Пользователь {} нажал отмену", chatId);
                SudokuCellDto[][] board = gameServiceclient.getBoard(chatId);
                SendMessage msg = uiHelper.buildBoardMessage(chatId, board);
                executeSafe(msg);
            }
            else if ("NEW_GAME".equals(data)) {
                log.info("Пользователь {} начал новую игру через кнопку", chatId);
                SendMessage msg = uiHelper.buildDifficultySelection(chatId);
                executeSafe(msg);
                return;
            }
        } catch (FeignException.NotFound ex) {
            log.warn("Игра не найдена для пользователя {}: {}", chatId, ex.getMessage());
            sendText(chatId, "\"⚠\uFE0F Игра не найдена. Начните новую игру командой /start\"");
        } catch (FeignException ex) {
            log.error("Ошибка соединения с игровым сервером для пользователя {}: {}", chatId, ex.getMessage());
            sendText(chatId, "⚠\uFE0F Ошибка соединения с игровым сервером. Попробуйте позже.");
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обработке запроса от пользователя {}: {}", chatId, e.getMessage(), e);
            sendText(chatId, "Произошла непредвиденная ошибка 😢");
        }
    }

    private String getRules() {
        return "📋 Правила игры Sudoku 4x4:\n\n" +
                "• Заполните сетку 4x4 числами от 1 до 4 \n" +
                "• В каждой строке должны быть все числа от 1 до 4 без повторений\n" +
                "• В каждом столбце должны быть все числа от 1 до 4 без повторений\n" +
                "• В каждом блоке 2x2 должны быть все числа от 1 до 4 без повторений\n\n" +
                "✏️ Как играть:\n" +
                "• Нажмите на клетку со знаком ❓ чтобы выбрать число\n" +
                "• Используйте кнопку 🧹 стереть для очистки клетки\n" +
                "• Используйте кнопку ↩️ отмена для отмены действия\n" +
                "• Цифры с точкой можно изменять, без точки изменять нельзя";
    }

    private void sendText(long chatId, String text) {
        log.info("Отправка текстового сообщения пользователю {}: {}", chatId, text);
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        executeSafe(message);
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

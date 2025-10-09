package ru.sudoku.game.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sudoku.game.service.GameService;

@Component
@RequiredArgsConstructor
public class SudokuBot extends TelegramLongPollingBot {
    private final GameService gameService;
    private final String botUserName = "sudoku_brain_bot";
    private final String botToken = System.getenv("TELEGRAM_SUDOKU_BOT_TOKEN");

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if("/start".equalsIgnoreCase(text)) {
            String board = gameService.newGame(chatId);
            sendText(chatId, "🧩 Новая игра судоку!\n\n" + board);
        }
        else {
            sendText(chatId,"Напиши /start, чтобы начать новую игру 😊" );
        }
    }
    private void sendText(long chatId, String text) {
        try {
            execute (SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
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

package ru.sudoku.game.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.sudoku.game.bot.SudokuBot;

@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final SudokuBot sudokuBot;
    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(sudokuBot);
            System.out.println("SudokuBot успешно зарегистрирован!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /*
    Зачем “регистрировать” бота в коде
TelegramLongPollingBot — это класс-клиент, который слушает Telegram через API.
Когда ты запускаешь Spring Boot, просто наличие класса SudokuBot не заставляет его автоматически слушать сервер Telegram.
Поэтому нужно вызвать метод, который подключается к Telegram, чтобы бот начал получать события.
В библиотеке Telegram для Java это делается через TelegramBotsApi.registerBot(bot) — иногда называют “регистрацией бота”.
     */
}

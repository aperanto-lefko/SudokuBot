package ru.sudoku.game.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.sudoku.game.bot.SudokuBot;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotInitializer {
    private final SudokuBot sudokuBot;
    @Value("${telegram.bot.token}")
    private String botToken;
    @PostConstruct
    public void init() {
        if (botToken == null || botToken.trim().isEmpty()) {
            throw new IllegalStateException("TELEGRAM_BOT_TOKEN не установлен!");
        }
        log.info("Токен бота загружен (длина: {})", botToken.length());

        // Регистрация бота
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(sudokuBot);
            log.info("SudokuBot успешно зарегистрирован!");
        } catch (TelegramApiException e) {
            log.error("Ошибка регистрации бота", e);
            throw new RuntimeException("Не удалось зарегистрировать бота", e);
        }
    }
    }

    /*
    Зачем “регистрировать” бота в коде
TelegramLongPollingBot — это класс-клиент, который слушает Telegram через API.
Когда ты запускаешь Spring Boot, просто наличие класса SudokuBot не заставляет его автоматически слушать сервер Telegram.
Поэтому нужно вызвать метод, который подключается к Telegram, чтобы бот начал получать события.
В библиотеке Telegram для Java это делается через TelegramBotsApi.registerBot(bot) — иногда называют “регистрацией бота”.
     */


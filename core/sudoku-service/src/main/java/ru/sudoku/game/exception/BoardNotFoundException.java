package ru.sudoku.game.exception;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException(long chatId) {
        super("Board nor found for chatId: " + chatId);
    }
}

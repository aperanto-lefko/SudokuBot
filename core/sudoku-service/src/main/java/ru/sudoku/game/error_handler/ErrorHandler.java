package ru.sudoku.game.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.sudoku.game.exception.BoardNotFoundException;

public class ErrorHandler extends BaseErrorHandler {

    // 404 Not Found
    @ExceptionHandler({
            BoardNotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }

    @Override
    protected String getFriendlyMessage(RuntimeException ex) {
        if (ex == null) {
            return "An unexpected error occurred";
        }

        String className = ex.getClass().getSimpleName();
        return switch (className) {
            case "BoardNotFoundException" -> "Board not found";
            default -> "An unexpected error occurred";
        };
    }
}

package ru.sudoku.game.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@Slf4j
public abstract class BaseErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorUserMessage = "Запрос составлен некорректно";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorUserMessage = "Внутренняя ошибка сервера";
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }

    protected ErrorResponse createErrorResponse(HttpStatus status,
                                                String message,
                                                Throwable ex) {
        return new ErrorResponse(
                ex.getCause(),
                getSafeStackTrace(ex),
                status.name(),
                message,
                ex.getMessage(),
                getSafeSuppressed(ex),
                ex.getLocalizedMessage());
    }

    protected ResponseEntity<ErrorResponse> handleException(RuntimeException ex, HttpStatus status) {
        String errorUserMessage = getFriendlyMessage(ex);
        logging(errorUserMessage, ex);
        return ResponseEntity
                .status(status)
                .body(createErrorResponse(
                        status,
                        errorUserMessage,
                        ex
                ));
    }

    protected abstract String getFriendlyMessage(RuntimeException ex);

    protected List<StackTraceElement> getSafeStackTrace(Throwable ex) {
        return ex.getStackTrace() != null ?
                Arrays.asList(ex.getStackTrace()) :
                Collections.emptyList();
    }

    protected List<Throwable> getSafeSuppressed(Throwable ex) {
        return ex.getSuppressed() != null && ex.getSuppressed().length > 0 ?
                Arrays.asList(ex.getSuppressed()) :
                null;
    }

    protected void logging(String message, Throwable ex) {
        log.error(message, ex.getMessage(), ex);
    }
}

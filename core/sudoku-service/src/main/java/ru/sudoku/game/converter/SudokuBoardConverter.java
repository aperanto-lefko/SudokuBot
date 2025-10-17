package ru.sudoku.game.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sudoku.game.exception.BoardConverterException;
import ru.sudoku.game.model.SudokuCell;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SudokuBoardConverter {
    ObjectMapper objectMapper;
    public String serializeBoard(SudokuCell[][] board) {
        try {
            return objectMapper.writeValueAsString(board);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации доски Sudoku", e);
            throw new BoardConverterException("Ошибка сериализации доски Sudoku");
        }
    }
    public SudokuCell[][] deserializeBoard(String boardJson) {
        try {
            return objectMapper.readValue(boardJson, SudokuCell[][].class);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации доски Sudoku", e);
            throw new BoardConverterException("Ошибка десериализации доски Sudoku");
        }
    }
}

package ru.sudoku.game.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sudoku.game.dto.SudokuCellDto;
import ru.sudoku.game.service.GameService;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameController {
    GameService gameService;

    @PostMapping("/new")
    public ResponseEntity<SudokuCellDto[][]> newGame(@RequestParam long chatId,
                                                     @RequestParam int blanks) {
        SudokuCellDto[][] board = gameService.newGame(chatId, blanks);
        return ResponseEntity.ok(board);
    }

    @PostMapping("/cell")
    public ResponseEntity<Void> setCell(@RequestParam long chatId,
                                        @RequestParam int row,
                                        @RequestParam int col,
                                        @RequestParam int value) {
        gameService.setCell(chatId, row, col, value);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/board/{chatId}")
    public ResponseEntity<SudokuCellDto[][]> getBoard(@PathVariable long chatId) {
        SudokuCellDto[][] board = gameService.getBoard(chatId);
        return ResponseEntity.ok(board);
    }

    @GetMapping("/{chatId}/solved")
    public ResponseEntity<Boolean> isSolved(@PathVariable long chatId) {
        Boolean solved = gameService.isSolved(chatId);
        return ResponseEntity.ok(solved);
    }
    @GetMapping("/{chatId}/full")
    public ResponseEntity<Boolean> isBoardFull(@PathVariable long chatId) {
        Boolean full = gameService.isBoardFull(chatId);
        return ResponseEntity.ok(full);
    }
}

package ru.sudoku.game.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sudoku.game.dto.SudokuCellDto;

@FeignClient(name = "sudoku-service")
public interface GameServiceClient {
    @PostMapping("/games/new")
    SudokuCellDto[][] newGame(@RequestParam long chatId, @RequestParam int blanks);

    @PostMapping("/games/cell")
    void setCell(@RequestParam long chatId,
                 @RequestParam int row,
                 @RequestParam int col,
                 @RequestParam int value);

    @GetMapping("/games/board/{chatId}")
    SudokuCellDto[][] getBoard(@PathVariable("chatId") long chatId);

    @GetMapping("/games/{chatId}/solved")
    boolean isSolved(@PathVariable("chatId") long chatId);

    @GetMapping("/games/{chatId}/full")
    boolean isBoardFull(@PathVariable("chatId") long chatId);
}

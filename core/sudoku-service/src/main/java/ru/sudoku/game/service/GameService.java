package ru.sudoku.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sudoku.game.generator.SudokuGenerator;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GameService {

    private final SudokuGenerator generator;
    private final ConcurrentHashMap<Long, int[][]> games = new ConcurrentHashMap<>();

    public String newGame (long chatId) {
        int[][] board = generator.generate(40);
        games.put(chatId, board);
        return renderBoard(board);
    }

    private String renderBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for(int r = 0; r<9; r++) {
            if (r%3 == 0) sb.append("\n");
            for (int c = 0; c < 9; c++) {
                if (c % 3 == 0) {
                    sb.append(" ");
                }
                sb.append(board[r][c] == 0 ? "· " : board[r][c] + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}

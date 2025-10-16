package ru.sudoku.game.mapper;

import org.mapstruct.Mapper;
import ru.sudoku.game.dto.SudokuCellDto;
import ru.sudoku.game.model.SudokuCell;

@Mapper(componentModel = "spring")
public interface SudokuCellMapper {
    SudokuCellDto[][] toDto (SudokuCell[][] cells);


}

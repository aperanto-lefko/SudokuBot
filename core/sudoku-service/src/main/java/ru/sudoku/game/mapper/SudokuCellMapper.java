package ru.sudoku.game.mapper;

import org.mapstruct.Mapper;
import ru.sudoku.game.dto.SudokuCellDto;
import ru.sudoku.game.model.SudokuCell;

@Mapper(componentModel = "spring")
public interface SudokuCellMapper {

    default SudokuCellDto[][] toDto(SudokuCell[][] cells) {
        if (cells == null) return null;
        SudokuCellDto[][] dto = new SudokuCellDto[cells.length][cells[0].length];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                SudokuCell cell = cells[i][j];
                dto[i][j] = new SudokuCellDto(cell.getValue(), cell.isFixed());
            }
        }
        return dto;
    }
}

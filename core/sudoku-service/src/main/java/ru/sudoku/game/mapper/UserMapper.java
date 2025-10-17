package ru.sudoku.game.mapper;

import org.mapstruct.Mapper;
import ru.sudoku.game.dto.UserDto;
import ru.sudoku.game.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

}

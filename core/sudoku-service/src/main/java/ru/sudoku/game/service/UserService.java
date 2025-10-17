package ru.sudoku.game.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sudoku.game.dto.RegisterRequest;
import ru.sudoku.game.dto.UserDto;
import ru.sudoku.game.exception.UserNotFoundException;
import ru.sudoku.game.mapper.UserMapper;
import ru.sudoku.game.model.User;
import ru.sudoku.game.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public UserDto register(RegisterRequest request) {
        Long chatId = request.getChatId();
        String username = request.getUsername();
        log.info("Создание нового пользователя chatId  {} username{}", chatId, username);
        User user = userRepository.findByChatId(chatId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .chatId(chatId)
                        .username(username)
                        .createdAt(request.getCreatedAt())
                        .consentGiven(request.isConsentGiven())
                        .build()));
        log.info("Пользователь успешно создан {}", user);
        return userMapper.toDto(user);
    }

    public boolean getConsent(Long chatId) {
        return getUserByChatId(chatId).isConsentGiven();
    }

    public void deleteUserData(Long chatId) {
        userRepository.findByChatId(chatId)
                .ifPresent(userRepository::delete);
    }

    private User getUserByChatId(Long chatId) {
        return  userRepository.findByChatId(chatId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с chatId " + chatId + " не найден"));
    }
}

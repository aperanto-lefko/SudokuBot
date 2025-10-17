package ru.sudoku.game.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sudoku.game.dto.RegisterRequest;
import ru.sudoku.game.dto.UserDto;
import ru.sudoku.game.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Регистрация пользователя с chatId: {}", request.getChatId());
        return ResponseEntity.ok(userService.register(request) );
    }

    @GetMapping("/consent/{chatId}")
    public ResponseEntity<Boolean> getConsent(@PathVariable Long chatId) {
        log.info("Проверка согласия пользователя с chatId: {}", chatId);
        boolean hasConsent = userService.getConsent(chatId);
        return ResponseEntity.ok(hasConsent);
    }
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long chatId) {
        log.info("Удаление данных пользователя с chatId: {}", chatId);
        userService.deleteUserData(chatId);
        return ResponseEntity.ok().build();
    }
}

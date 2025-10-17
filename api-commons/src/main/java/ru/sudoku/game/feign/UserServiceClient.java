package ru.sudoku.game.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.sudoku.game.dto.RegisterRequest;
import ru.sudoku.game.dto.UserDto;

@FeignClient(name = "sudoku-service")
public interface UserServiceClient {

    @PostMapping("/users/register")
    UserDto register(@RequestBody RegisterRequest request);

    @PostMapping("/users/consent/{chatId}")
    void giveConsent(@PathVariable("chatId") Long chatId);

    @GetMapping("/users/consent/{chatId}")
    Boolean getConsent(@PathVariable("chatId") Long chatId);

    @DeleteMapping("/users/{chatId}")
    void deleteUser(@PathVariable("chatId") Long chatId);
}

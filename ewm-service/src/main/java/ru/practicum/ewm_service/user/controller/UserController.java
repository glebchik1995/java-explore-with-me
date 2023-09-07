package ru.practicum.ewm_service.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.user.dto.NewUserDto;
import ru.practicum.ewm_service.user.dto.UserDto;
import ru.practicum.ewm_service.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.ewm_service.util.Constant.DEF_VAL_FROM;
import static ru.practicum.ewm_service.util.Constant.DEF_VAL_SIZE;

@Validated
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserDto newUserDto) {
        log.info("POST запрос на создание пользователя");
        return userService.createUser(newUserDto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                        @RequestParam(defaultValue = DEF_VAL_FROM) Integer from,
                                        @RequestParam(defaultValue = DEF_VAL_SIZE) Integer size) {
        log.info("GET запрос на получение списка пользователей");
        return userService.getAllUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        log.info("DELETE запрос на удаление пользователя c ID = {}", userId);
        userService.deleteUserById(userId);
    }
}

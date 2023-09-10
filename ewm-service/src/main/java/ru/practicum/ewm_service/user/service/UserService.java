package ru.practicum.ewm_service.user.service;

import ru.practicum.ewm_service.user.dto.NewUserDto;
import ru.practicum.ewm_service.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserDto newUserDto);

    List<UserDto> getAllUsers(List<Long> ids, int from, int size);

    void deleteUserById(long userId);
}

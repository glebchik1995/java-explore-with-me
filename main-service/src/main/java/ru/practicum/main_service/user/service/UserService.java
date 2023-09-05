package ru.practicum.main_service.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers(List<Long> ids, Pageable pageable);

    UserDto saveUser(NewUserRequest newUserRequest);

    void deleteUserById(Long id);

}
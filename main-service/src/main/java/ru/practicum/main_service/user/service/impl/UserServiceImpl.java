package ru.practicum.main_service.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.error.exception.DataAlreadyExistException;
import ru.practicum.main_service.error.exception.DataNotFoundException;
import ru.practicum.main_service.user.repository.UserRepository;
import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;
import ru.practicum.main_service.user.model.UserMapper;
import ru.practicum.main_service.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto saveUser(NewUserRequest newUserRequest) {
        log.info("METHOD: SAVE_USER");
        if (userRepository.findByName(newUserRequest.getName()) != null) {
            throw new DataAlreadyExistException(String.format(
                    "Пользователь с именем: %s уже существует", newUserRequest.getName()));
        }
        log.info("Сохраняем пользователя с именем = {}", newUserRequest.getName());
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserModel(newUserRequest)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers(List<Long> ids, Pageable pageable) {
        log.info("Получаем список всех пользователей");
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        log.info("METHOD: DELETE_USER_BY_ID");
        checkUser(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с ID = {} удален", userId);
    }

    private void checkUser(Long userId) {
        log.info("Совершаем поиск пользователя с ID = {}", userId);
        userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException(String.format("Пользователь с ID = %d не найден", userId)));
    }

}
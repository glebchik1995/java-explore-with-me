package ru.practicum.ewm_service.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm_service.exception.exception.ConflictException;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;
import ru.practicum.ewm_service.user.dto.NewUserDto;
import ru.practicum.ewm_service.user.dto.UserDto;
import ru.practicum.ewm_service.user.mapper.UserMapper;
import ru.practicum.ewm_service.user.model.User;
import ru.practicum.ewm_service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserDto newUserDto) {
        if (userRepository.findByName(newUserDto.getName()) != null) {
            throw new ConflictException(String.format(
                    "Пользователь с именем: %s уже существует", newUserDto.getName()));
        }
        User user = userRepository.save(UserMapper.toUserModel(newUserDto));
        log.info("Сохраняем пользователя с именем = {}", newUserDto.getName());
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        Page<User> answer = CollectionUtils.isEmpty(ids) ? userRepository.findAll(page) :
                userRepository.findAllByIdIn(ids, page);
        log.info("Получаем список всех пользователей");
        return answer.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException(String.format("Пользователь с ID = %d не найден", userId)));
        userRepository.delete(user);
        log.info("Пользователь с ID = {} удален", userId);
    }
}

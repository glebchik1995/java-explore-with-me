package ru.practicum.main_service.user.model;

import lombok.experimental.UtilityClass;
import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;
import ru.practicum.main_service.user.dto.UserShortDto;

@UtilityClass
public class UserMapper {

    public User toUserModel(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

}

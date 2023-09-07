package ru.practicum.ewm_service.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm_service.user.dto.NewUserDto;
import ru.practicum.ewm_service.user.dto.UserDto;
import ru.practicum.ewm_service.user.dto.UserDtoShort;
import ru.practicum.ewm_service.user.model.User;

@UtilityClass
public class UserMapper {

    public User toUserModel(NewUserDto newUserDto) {
        return User.builder()
                .name(newUserDto.getName())
                .email(newUserDto.getEmail())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserDtoShort toUserDtoShort(User user) {
        return UserDtoShort.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

}

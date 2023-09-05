package ru.practicum.main_service.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long id;

    @Size(min = 2, max = 250)
    @NotBlank
    String name;

    @Size(min = 6, max = 254)
    String email;
}
package ru.practicum.ewm_service.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserDto {

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    String email;

    @Size(min = 2, max = 250)
    @NotBlank
    String name;
}

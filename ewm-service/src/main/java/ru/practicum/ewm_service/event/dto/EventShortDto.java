package ru.practicum.ewm_service.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.user.dto.UserDtoShort;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    String annotation;
    String title;
    CategoryDto category;
    Long confirmedRequests;
    Long views;
    LocalDateTime eventDate;
    UserDtoShort initiator;
    Boolean paid;
}

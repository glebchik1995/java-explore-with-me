package ru.practicum.ewm_service.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm_service.util.enums.Status;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    Status status;
}

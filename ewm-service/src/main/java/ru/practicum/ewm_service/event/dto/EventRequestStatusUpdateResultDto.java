package ru.practicum.ewm_service.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResultDto {

    List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

    List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
}

package ru.practicum.ewm_service.event.service;

import ru.practicum.ewm_service.event.dto.*;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventPrivateService {

    EventDto createNewEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventsByUser(Long userId, Integer from, Integer size);

    EventDto getEventsByUser(Long userId, Long eventId);

    EventDto updateEventsByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsByUser(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto updateStatusOfRequests(Long userId, Long eventId, EventRequestStatusUpdateRequestDto eventRequestUpdate);
}

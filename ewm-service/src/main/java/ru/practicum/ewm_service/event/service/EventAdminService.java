package ru.practicum.ewm_service.event.service;

import ru.practicum.ewm_service.event.dto.EventDto;
import ru.practicum.ewm_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm_service.util.enums.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {
    List<EventDto> searchEvents(List<Long> users, List<State> states, List<Long> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}

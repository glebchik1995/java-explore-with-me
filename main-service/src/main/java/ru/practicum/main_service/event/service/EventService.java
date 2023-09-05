package ru.practicum.main_service.event.service;


import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.enums.EventSort;
import ru.practicum.main_service.event.model.enums.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    EventFullDto findPublicEventById(Long id, HttpServletRequest request);

    List<EventShortDto> findAllPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                                          Pageable pageable, HttpServletRequest request);

    List<EventFullDto> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto findPrivateEventById(Long userId, Long eventId);

    List<EventShortDto> findAllEvents(Long userId, Pageable pageable);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<Event> getEventsByIds(List<Long> eventsId);

    List<EventShortDto> toEventsShortDto(List<Event> events);
}
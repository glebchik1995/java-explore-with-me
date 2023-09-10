package ru.practicum.ewm_service.event.service;

import ru.practicum.ewm_service.event.dto.EventDto;
import ru.practicum.ewm_service.util.enums.Sorts;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicService {
    List<EventDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                             LocalDateTime rangeEnd, Boolean onlyAvailable, Sorts sorts, Integer from, Integer size,
                             HttpServletRequest request);

    EventDto getEventById(Long id, HttpServletRequest request);
}

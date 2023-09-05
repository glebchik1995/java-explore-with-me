package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.request.dto.RequestDto;
import ru.practicum.main_service.request.service.RequestService;
import ru.practicum.main_service.utils.EwmPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
@Slf4j
public class EventPrivateController {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST запрос на создание события: {} от пользователя с ID= {}", newEventDto, userId);
        return eventService.saveEvent(userId, newEventDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findAllEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET запрос на получение списка всех событий от пользователя с ID= {}", userId);
        return eventService.findAllEvents(userId, EwmPageRequest.of(from, size));
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("GET запрос на получение события с ID= {} от пользователя с ID= {}", eventId, userId);
        return eventService.findPrivateEventById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> findEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("GET запрос на получение от инициатора с ID= {} события с ID= {}", userId, eventId);
        return requestService.findRequestsByEventOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("PATCH запрос на изменение события с ID= {} от пользователя с ID= {}", eventId, userId);
        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("PATCH запрос на изменение статуса запросов события с ID= {} от пользователя с ID= {}", eventId, userId);
        return requestService.updateRequestsByEventOwner(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
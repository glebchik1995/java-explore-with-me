package ru.practicum.ewm_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.event.dto.*;
import ru.practicum.ewm_service.event.service.EventPrivateService;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm_service.util.Constant.DEF_VAL_FROM;
import static ru.practicum.ewm_service.util.Constant.DEF_VAL_SIZE;

@Validated
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {

    private final EventPrivateService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createNewEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST запрос на создание события: {} от пользователя с ID= {}", newEventDto, userId);
        return eventService.createNewEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getAllEventsByUser(@PathVariable Long userId,
                                                  @RequestParam(value = "from", defaultValue = DEF_VAL_FROM) @PositiveOrZero Integer from,
                                                  @RequestParam(value = "size", defaultValue = DEF_VAL_SIZE) @Positive Integer size) {
        log.info("GET запрос на получение списка всех событий от пользователя с ID= {}", userId);
        return eventService.getAllEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventsByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET запрос на получение от пользователя с ID= {} события с ID= {}", userId, eventId);
        return eventService.getEventsByUser(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        log.info("GET запрос на получение запросов события с ID= {} от пользователя с ID= {}", eventId, userId);
        return eventService.getRequestsByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventsByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                       @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("PATCH запрос на изменение события с ID= {} от пользователя с ID= {}", eventId, userId);
        return eventService.updateEventsByUser(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateStatusOfRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                                    @Valid @RequestBody
                                                                    EventRequestStatusUpdateRequestDto eventRequestUpdate) {
        log.info("PATCH запрос на изменение статуса запросов события с ID= {} от пользователя с ID= {}", eventId, userId);
        return eventService.updateStatusOfRequests(userId, eventId, eventRequestUpdate);
    }
}

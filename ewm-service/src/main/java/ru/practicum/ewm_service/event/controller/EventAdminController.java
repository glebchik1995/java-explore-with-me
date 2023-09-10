package ru.practicum.ewm_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.event.dto.EventDto;
import ru.practicum.ewm_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm_service.event.service.EventAdminService;
import ru.practicum.ewm_service.util.enums.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm_service.util.Constant.*;

@Validated
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {

    private final EventAdminService eventService;

    @GetMapping
    public List<EventDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(value = "from", defaultValue = DEF_VAL_FROM) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = DEF_VAL_SIZE) @Positive Integer size) {
        log.info("GET запрос на получение списка событий");
        return eventService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventByAdmin(@PathVariable Long eventId,
                                       @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("PATCH запрос на обновление списка событий");
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}

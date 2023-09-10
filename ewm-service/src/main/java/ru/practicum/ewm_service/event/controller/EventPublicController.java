package ru.practicum.ewm_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.event.dto.EventDto;
import ru.practicum.ewm_service.event.service.EventPublicService;
import ru.practicum.ewm_service.util.enums.Sorts;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm_service.util.Constant.*;

@Validated
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {

    private final EventPublicService eventService;

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        log.info("GET запрос на получение события с ID = {}", id);
        return eventService.getEventById(id, request);
    }

    @GetMapping
    public List<EventDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) Sorts sorts,
            @RequestParam(defaultValue = DEF_VAL_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = DEF_VAL_SIZE) @Positive Integer size,
            HttpServletRequest request) {
        log.info("GET запрос на получение всех событий");
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sorts, from, size, request);
    }

}

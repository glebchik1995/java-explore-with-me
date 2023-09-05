package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.EndpointHitResponseDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.util.Constant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class StatsController {

    private final EndpointHitService service;

    @PostMapping(value = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitResponseDto saveHit(@Valid @RequestBody EndpointHitRequestDto dto) {
        log.info("POST hit={}", dto);
        return service.saveEndpointHit(dto);
    }

    @GetMapping(value = "/stats")
    public List<ViewStatsDto> getViewStats(@RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("GET stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return service.getViewStats(start, end, uris, unique);
    }
}

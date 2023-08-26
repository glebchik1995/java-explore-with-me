package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.EndpointHitResponseDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import static ru.practicum.stats.util.Constant.TIME_PATTERN;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final EndpointHitService service;

    @PostMapping("/hit")
    public EndpointHitResponseDto saveHit(@Valid @RequestBody EndpointHitRequestDto dto) {
        log.info("POST hit={}", dto);
        return service.saveEndpointHit(dto);
    }

    @GetMapping("/stats")
    public Collection<ViewStatsDto> getViewStats(@RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime start,
                                                 @RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime end,
                                                 @RequestParam(required = false) Set<String> uris,
                                                 @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("GET stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return service.getViewStats(start, end, uris, unique);
    }
}

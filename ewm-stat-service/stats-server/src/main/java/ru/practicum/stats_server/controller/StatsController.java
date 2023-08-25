package ru.practicum.stats_server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats_server.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final EndpointHitService service;

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public EndpointHitDto saveHit(@Valid @RequestBody EndpointHitDto dto) {
        log.info("POST hit={}", dto);
        return service.save(dto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getViewStats(@DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
                                           @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("GET stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return service.getViewStats(start, end, uris, unique);
    }
}

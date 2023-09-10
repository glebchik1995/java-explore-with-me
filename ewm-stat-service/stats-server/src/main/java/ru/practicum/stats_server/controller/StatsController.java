package ru.practicum.stats_server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats_server.service.EndpointHitService;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final EndpointHitService statsService;

    @PostMapping(value = "/hit", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody EndpointHitRequestDto dto) {
        log.info("POST запрос на сохранение информации по эндпойнту: {}", dto.getUri());
        statsService.createHit(dto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, Charset.defaultCharset()));
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, Charset.defaultCharset()));
        log.info("GET запрос на получение статистики: start = {} end = {} uris: {} unique = {}",
                start, end, uris, unique);
        return statsService.getStats(startTime, endTime, uris, unique);
    }

    @GetMapping("/view/{eventId}")
    public Long getView(@PathVariable Long eventId) {
        return statsService.getViewStats(eventId);
    }
}
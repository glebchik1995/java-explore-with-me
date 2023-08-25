package ru.practicum.stats_client.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.stats_client.client.StatClient;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@Slf4j
public class ClientController {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private StatClient statClient;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Object> saveHit(@Valid @RequestBody EndpointHitDto dto) {
        log.info("POST hit={}", dto);
        return statClient.saveHit(dto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getViewStats(@RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
                                               @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
                                               @RequestParam(required = false, name = "uris") List<String> uris,
                                               @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("GET stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statClient.getViewStats(start, end, uris, unique);
    }
}

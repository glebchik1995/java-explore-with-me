package ru.practicum.stats.client.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitRequestDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    public final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> postHit(@Valid @RequestBody EndpointHitRequestDto endpointHit) {
        return statsClient.postHit(endpointHit);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        return statsClient.getStats(start, end, uris, unique);
    }

}

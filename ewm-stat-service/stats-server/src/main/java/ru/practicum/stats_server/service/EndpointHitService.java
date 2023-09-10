package ru.practicum.stats_server.service;

import ru.practicum.dto.EndpointHitRequestDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {
    void createHit(EndpointHitRequestDto endpointHitDto);

    Long getViewStats(Long eventId);

    List<ViewStatsDto> getStats(LocalDateTime start,
                                LocalDateTime end,
                                List<String> uris,
                                Boolean unique);

}

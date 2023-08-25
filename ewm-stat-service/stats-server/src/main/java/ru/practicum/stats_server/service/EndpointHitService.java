package ru.practicum.stats_server.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

public interface EndpointHitService {
    EndpointHitDto save(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getViewStats(String start, String end, List<String> uri, Boolean unique);
}

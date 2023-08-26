package ru.practicum.stats.service;

import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.EndpointHitResponseDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EndpointHitService {

    EndpointHitResponseDto saveEndpointHit(EndpointHitRequestDto endpointHitRequestDto);

    Collection<ViewStatsDto>  getViewStats(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris,
                                           Boolean unique);

}

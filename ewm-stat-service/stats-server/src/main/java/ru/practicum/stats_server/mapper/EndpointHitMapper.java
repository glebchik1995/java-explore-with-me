package ru.practicum.stats_server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitRequestDto;
import ru.practicum.stats_server.model.EndpointHit;
@UtilityClass
public class EndpointHitMapper {
    public EndpointHit toEndpointHit(EndpointHitRequestDto dto) {
        return EndpointHit.builder()
                .ip(dto.getIp())
                .app(dto.getApp())
                .uri(dto.getUri())
                .timestamp(dto.getTimestamp())
                .build();
    }
}

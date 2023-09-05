package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.EndpointHitResponseDto;
import ru.practicum.stats.model.EndpointHit;

@UtilityClass
public class EndpointHitMapper {

    public static EndpointHit toEndpointHitModel(EndpointHitRequestDto endpointHitRequestDto) {
        return EndpointHit.builder()
                .app(endpointHitRequestDto.getApp())
                .ip(endpointHitRequestDto.getIp())
                .uri(endpointHitRequestDto.getUri())
                .timestamp(endpointHitRequestDto.getTimestamp())
                .build();
    }

    public static EndpointHitResponseDto toEndpointHitDto(EndpointHit hit) {
        return EndpointHitResponseDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .ip(hit.getIp())
                .uri(hit.getUri())
                .timestamp(hit.getTimestamp())
                .build();
    }
}
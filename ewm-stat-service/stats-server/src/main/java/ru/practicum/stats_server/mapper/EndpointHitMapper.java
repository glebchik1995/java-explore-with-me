package ru.practicum.stats_server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats_server.entity.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EndpointHitMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static EndpointHit toEndpointHitModel(EndpointHitDto hitDto) {
        return EndpointHit.builder()
                .id(hitDto.getId())
                .app(hitDto.getApp())
                .ip(hitDto.getIp())
                .uri(hitDto.getUri())
                .timestamp(LocalDateTime.parse(hitDto.getTimestamp().format(formatter)))
                .build();
    }

    public static EndpointHitDto toEndpointHitDto(EndpointHit hit) {
        return EndpointHitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .ip(hit.getIp())
                .uri(hit.getUri())
                .timestamp(LocalDateTime.parse(hit.getTimestamp().format(formatter)))
                .build();
    }

    public static ViewStatsDto toViewStatsDto(EndpointHit hit) {
        return ViewStatsDto.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .build();
    }
}

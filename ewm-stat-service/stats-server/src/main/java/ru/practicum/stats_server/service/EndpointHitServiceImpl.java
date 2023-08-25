package ru.practicum.stats_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats_server.entity.EndpointHit;
import ru.practicum.stats_server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats_server.mapper.EndpointHitMapper.toEndpointHitDto;
import static ru.practicum.stats_server.mapper.EndpointHitMapper.toEndpointHitModel;


@Service
@RequiredArgsConstructor
@Transactional
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository repository;

    @Override
    public EndpointHitDto save(EndpointHitDto dto) {
        EndpointHit endpointHit = toEndpointHitModel(dto);
        return toEndpointHitDto(repository.save(endpointHit));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> getViewStats(LocalDateTime startTime, LocalDateTime endTime, List<String> uri, Boolean unique) {
        if (uri == null) {
            return unique ? repository.getAllUniqueWhereCreatedBetweenStartAndEnd(startTime, endTime) :
                    repository.getAllWhereCreatedBetweenStartAndEnd(startTime, endTime);
        } else {
            return unique ? repository.getAllUniqueWhereCreatedBetweenStartAndEndAndUriInList(startTime, endTime, uri) :
                    repository.getAllWhereCreatedBetweenStartAndEndAndUriInList(startTime, endTime, uri);
        }
    }
}


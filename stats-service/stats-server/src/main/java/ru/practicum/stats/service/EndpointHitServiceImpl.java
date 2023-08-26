package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.EndpointHitResponseDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.stats.mapper.EndpointHitMapper.toEndpointHitDto;
import static ru.practicum.stats.mapper.EndpointHitMapper.toEndpointHitModel;

@Service
@RequiredArgsConstructor
@Transactional
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository repository;

    @Override
    public EndpointHitResponseDto saveEndpointHit(EndpointHitRequestDto dto) {
        EndpointHit endpointHit = toEndpointHitModel(dto);
        return toEndpointHitDto(repository.save(endpointHit));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ViewStatsDto> getViewStats(LocalDateTime start,
                                                 LocalDateTime end,
                                                 List<String> uris,
                                                 Boolean unique) {

        return
                unique
                        ? repository.getAllHitsByTimestampAndUriUnique(uris, start, end)
                        : repository.getAllByTimestampAndUriNotUnique(uris, start, end);
    }
}


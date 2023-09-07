package ru.practicum.stats_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.dto.EndpointHitRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats_server.mapper.EndpointHitMapper;
import ru.practicum.stats_server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository hitsRepository;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return (CollectionUtils.isEmpty(uris)) ?
                (unique ? hitsRepository.findAllDistinct(start, end) : hitsRepository.findAll(start, end)) :
                (unique ? hitsRepository.findAllByUriDistinct(start, end, uris) :
                        hitsRepository.findAllByUri(start, end, uris));
    }

    @Override
    @Transactional
    public void createHit(EndpointHitRequestDto dto) {
        hitsRepository.save(EndpointHitMapper.toEndpointHit(dto));
    }

    @Override
    public Long getViewStats(Long eventId) {
        Long view = hitsRepository.countDistinctByUri("/events/" + eventId);
        return Objects.requireNonNullElse(view, 0L);
    }
}

package ru.practicum.main_service.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.client.StatsClient;
import ru.practicum.main_service.error.exception.ValidationException;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.service.StatsService;
import ru.practicum.main_service.request.repository.RequestRepository;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value(value = "${app.name}")
    private String appName;

    @Override
    public void addHit(HttpServletRequest request) {
        EndpointHitRequestDto endpointHit = EndpointHitRequestDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.postHit(endpointHit);
    }


    @Override
    public Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();
        List<Event> publishedEvents = getPublished(events);
        if (events.isEmpty()) {
            return views;
        }
        Optional<LocalDateTime> minPublishedOn = getMinPublishedOn(publishedEvents);
        if (minPublishedOn.isPresent()) {
            LocalDateTime start = minPublishedOn.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = getURIs(publishedEvents);
            List<ViewStatsDto> stats = getStats(start, end, uris);
            putStats(views, stats);
        }
        return views;
    }

    @Override
    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> eventsId = getPublished(events).stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> requestStats = new HashMap<>();
        if (!eventsId.isEmpty()) {
            requestRepository.findConfirmedRequests(eventsId)
                    .forEach(stat -> requestStats.put(stat.getEventId(), stat.getConfirmedRequests()));
        }
        return requestStats;
    }

    private Optional<LocalDateTime> getMinPublishedOn(List<Event> publishedEvents) {
        return publishedEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);
    }

    private List<String> getURIs(List<Event> publishedEvents) {
        return publishedEvents.stream()
                .map(Event::getId)
                .map(id -> ("/events/" + id))
                .collect(Collectors.toList());
    }

    private void putStats(Map<Long, Long> views, List<ViewStatsDto> stats) {
        stats.forEach(stat -> {
            Long eventId = Long.parseLong(stat.getUri()
                    .split("/", 0)[2]);
            views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
        });
    }

    private List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, true);
        try {
            return Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));
        } catch (JsonProcessingException e) {
            throw new ValidationException("");
        }
    }

    private List<Event> getPublished(List<Event> events) {
        return events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
    }

}

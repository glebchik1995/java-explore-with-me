package ru.practicum.ewm_service.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.client.Client;
import ru.practicum.ewm_service.event.dto.EventDto;
import ru.practicum.ewm_service.event.mapper.EventMapper;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.repository.EventRepository;
import ru.practicum.ewm_service.event.service.EventPublicService;
import ru.practicum.ewm_service.exception.exception.BadRequestException;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;
import ru.practicum.ewm_service.request.repository.RequestRepository;
import ru.practicum.ewm_service.util.enums.Sorts;
import ru.practicum.ewm_service.util.enums.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;
    private final Client statClient;
    private final RequestRepository requestRepository;

    @Override
    public List<EventDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd, Boolean onlyAvailable, Sorts sorts, Integer from,
                                    Integer size, HttpServletRequest request) {
        if (rangeEnd != null && rangeStart != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Неверно задано дата и время");
        }
        PageRequest page = PageRequest.of(from / size, size);
        List<EventDto> answer;
        if (rangeEnd == null && rangeStart == null) {
            answer = eventRepository.findAllByPublicNoDate(text, categories, paid, LocalDateTime.now(), page).stream()
                    .map(EventMapper::toEventDto).collect(Collectors.toList());
        } else {
            answer = eventRepository.findAllByPublic(text, categories, paid, rangeStart, rangeEnd, page).stream()
                    .map(EventMapper::toEventDto).collect(Collectors.toList());
        }
        if (sorts != null) {
            switch (sorts) {
                case EVENT_DATE:
                    answer.sort(Comparator.comparing(EventDto::getEventDate));
                    break;
                case VIEWS:
                    answer.sort(Comparator.comparing(EventDto::getViews));
                    break;
            }
        }
        if (onlyAvailable) {
            return answer.stream().filter(eventDto -> eventDto.getParticipantLimit() > eventDto.getConfirmedRequests()
                            || eventDto.getParticipantLimit() == 0).collect(Collectors.toList());
        }
        statClient.createStat(request);
        return answer;
    }

    @Override
    public EventDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(()
                -> new DataNotFoundException(String.format("Событие с ID = %d не найдено", eventId)));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataNotFoundException("Событие должно быть опубликовано");
        }
        statClient.createStat(request);

        EventDto eventDto = EventMapper.toEventDto(event);
        eventDto.setConfirmedRequests(requestRepository.findConfirmedRequests(eventDto.getId()));
        eventDto.setViews(statClient.getView(eventDto.getId()));
        statClient.createStat(request);
        return eventDto;
    }
}

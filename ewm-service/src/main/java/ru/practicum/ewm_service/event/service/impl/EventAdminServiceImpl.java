package ru.practicum.ewm_service.event.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.category.repository.CategoryRepository;
import ru.practicum.ewm_service.client.Client;
import ru.practicum.ewm_service.event.dto.EventDto;
import ru.practicum.ewm_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm_service.event.mapper.EventMapper;
import ru.practicum.ewm_service.event.mapper.LocationMapper;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.Location;
import ru.practicum.ewm_service.event.repository.EventRepository;
import ru.practicum.ewm_service.event.repository.LocationRepository;
import ru.practicum.ewm_service.event.service.EventAdminService;
import ru.practicum.ewm_service.exception.exception.BadRequestException;
import ru.practicum.ewm_service.exception.exception.ConflictException;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;
import ru.practicum.ewm_service.request.repository.RequestRepository;
import ru.practicum.ewm_service.util.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm_service.util.enums.State.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final Client client;

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> searchEvents(List<Long> users, List<State> states, List<Long> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeEnd != null && rangeStart != null && rangeStart.isAfter(rangeEnd)) {
            throw new IllegalArgumentException("Неверно задано дата и время");
        }
        PageRequest page = PageRequest.of(from / size, size);
        List<EventDto> answer = eventRepository.findAllAdminByData(users, states, categories, rangeStart, rangeEnd, page)
                .stream()
                .map(EventMapper::toEventDto)
                .collect(Collectors.toList());
        answer.forEach(e ->
                e.setConfirmedRequests(requestRepository.findConfirmedRequests(e.getId())));
        answer.forEach(e -> e.setViews(client.getView(e.getId())));
        return answer;
    }

    @Override
    public EventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {

        Event event = eventRepository.findById(eventId).orElseThrow(()
                -> new DataNotFoundException(String.format("Событие с ID = %d не найдено", eventId)));

        if (updateEvent.getEventDate() != null && updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Неверно задано дата и время");
        }
        if (updateEvent.getStateAction() != null) {
            if (!event.getState().equals(PENDING)) {
                throw new ConflictException("Изменять можно только события со статусом: PENDING");
            }
            switch (updateEvent.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                default:
                    throw new IllegalArgumentException("Недопустимое значение состояния");
            }
        }
        if (updateEvent.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() ->
                    new DataNotFoundException(String.format(
                            "Категория с ID = %d не найдена", updateEvent.getCategory()))));
        }
        if (updateEvent.getLocation() != null) {
            System.out.println(updateEvent.getLocation());
            Location location = locationRepository.findByLatAndLon(updateEvent.getLocation().getLat(),
                            updateEvent.getLocation().getLat())
                    .orElseGet(() -> locationRepository.save(LocationMapper.toLocationModel(updateEvent.getLocation())));
            event.setLocation(location);
        }
        Optional.ofNullable(updateEvent.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEvent.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEvent.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEvent.getTitle()).ifPresent(event::setTitle);
        EventDto eventDto = EventMapper.toEventDto(event);
        eventDto.setConfirmedRequests(requestRepository.findConfirmedRequests(eventDto.getId()));
        eventDto.setViews(client.getView(eventDto.getId()));
        return eventDto;
    }
}

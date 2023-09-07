package ru.practicum.ewm_service.event.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.category.model.Category;
import ru.practicum.ewm_service.category.repository.CategoryRepository;
import ru.practicum.ewm_service.event.dto.*;
import ru.practicum.ewm_service.event.mapper.EventMapper;
import ru.practicum.ewm_service.event.mapper.LocationMapper;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.Location;
import ru.practicum.ewm_service.event.repository.EventRepository;
import ru.practicum.ewm_service.event.repository.LocationRepository;
import ru.practicum.ewm_service.event.service.EventPrivateService;
import ru.practicum.ewm_service.exception.exception.BadRequestException;
import ru.practicum.ewm_service.exception.exception.ConflictException;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm_service.request.mapper.RequestMapper;
import ru.practicum.ewm_service.request.model.ParticipationRequest;
import ru.practicum.ewm_service.request.repository.RequestRepository;
import ru.practicum.ewm_service.user.model.User;
import ru.practicum.ewm_service.user.repository.UserRepository;
import ru.practicum.ewm_service.util.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm_service.util.enums.State.*;
import static ru.practicum.ewm_service.util.enums.Status.CONFIRMED;
import static ru.practicum.ewm_service.util.enums.Status.REJECTED;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventPrivateServiceImpl implements EventPrivateService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;

    @Override
    public EventDto createNewEvent(Long userId, NewEventDto newEventDto) {

        if (newEventDto.getEventDate() != null && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Неверно задано дата и время");
        }

        User user = checkUser(userId);

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new DataNotFoundException(String.format(
                        "Категория с ID = %d не найдена", newEventDto.getCategory())));

        Location location = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLat()).orElseGet(() ->
                locationRepository.save(LocationMapper.toLocationModel(newEventDto.getLocation())));

        Event event = eventRepository.save(eventMapper.toEventModel(newEventDto, category, location, user,
                LocalDateTime.now(), PENDING));

        return eventMapper.toEventDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEventsByUser(Long userId, Integer from, Integer size) {
        User user = checkUser(userId);
        PageRequest page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiator(user, page).stream().map(eventMapper::toEventDtoShort).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventsByUser(Long userId, Long eventId) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        if (!event.getInitiator().equals(user)) {
            throw new IllegalArgumentException(String.format(
                    "Событие с ID = %d не принадлежит пользователю с ID = %d", eventId, userId));
        }
        return eventMapper.toEventDto(event);
    }

    @Override
    public EventDto updateEventsByUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException(String.format(
                    "Событие с ID = %d не принадлежит пользователю с ID = %d", eventId, userId));
        }
        if (event.getState().equals(PUBLISHED)) {
            throw new ConflictException("Изменять можно только события со статусом: PENDING");
        }
        if (updateEvent.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() ->
                    new DataNotFoundException(String.format(
                            "Категория с ID = %d не найдено", updateEvent.getCategory()))));
        }
        if (updateEvent.getEventDate() != null && updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Неверно задано дата и время");
        }
        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(PENDING);
                    break;
                default:
                    throw new IllegalArgumentException("Недопустимое значение состояния");
            }
        }
        if (updateEvent.getLocation() != null) {
            Location location = locationRepository.findByLatAndLon(updateEvent.getLocation().getLat(),
                            updateEvent.getLocation().getLat())
                    .orElseGet(() -> locationRepository.save(LocationMapper.toLocationModel(updateEvent.getLocation())));
            event.setLocation(location);
        }
        Optional.ofNullable(updateEvent.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEvent.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEvent.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEvent.getTitle()).ifPresent(event::setTitle);
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUser(Long userId, Long eventId) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        if (!event.getInitiator().equals(user)) {
            throw new IllegalArgumentException(String.format(
                    "Событие с ID = %d не принадлежит пользователю с ID = %d", eventId, userId));
        }
        return requestRepository.findAllByEvent(event).stream()
                .map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResultDto updateStatusOfRequests(Long userId, Long eventId,
                                                                    EventRequestStatusUpdateRequestDto eventRequestUpdate) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);

        if (!event.getInitiator().equals(user)) {
            throw new IllegalArgumentException(String.format(
                    "Событие с ID = %d не принадлежит пользователю с ID = %d", eventId, userId));
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new IllegalArgumentException(String.format(
                    "Для события с ID = %d подтвреждение заявок не требуется", eventId));
        }

        if (event.getParticipantLimit().equals(requestRepository.findConfirmedRequests(eventId))) {
            throw new ConflictException(String.format("Для события с ID = %d достигнут лимит заявок", eventId));
        }

        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(eventRequestUpdate.getRequestIds());
        if (!requests.stream().map(ParticipationRequest::getStatus).allMatch(Status.PENDING::equals)) {
            throw new ConflictException("Изменять можно только события со статусом: PENDING");
        }

        EventRequestStatusUpdateResultDto result = new EventRequestStatusUpdateResultDto();

        if (eventRequestUpdate.getStatus().equals(REJECTED)) {
            requests.forEach(request -> request.setStatus(REJECTED));
            result.setRejectedRequests(requestRepository.saveAll(requests).stream()
                    .map(RequestMapper::toRequestDto).collect(Collectors.toList()));
        }

        if (eventRequestUpdate.getStatus().equals(CONFIRMED)) {
            for (ParticipationRequest r : requests) {
                if (requestRepository.findConfirmedRequests(eventId).equals(event.getParticipantLimit())) {
                    r.setStatus(REJECTED);
                    result.getRejectedRequests().add(RequestMapper.toRequestDto(requestRepository.save(r)));
                } else {
                    r.setStatus(CONFIRMED);
                    result.getConfirmedRequests().add(RequestMapper.toRequestDto(requestRepository.save(r)));
                }
            }
        }
        eventRepository.save(event);
        return result;
    }

    private User checkUser(Long userId) {
        log.info("Совершаем поиск пользователя с ID = {}", userId);
        return userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException(String.format("Пользователь с ID = %d не найден", userId)));
    }

    private Event checkEvent(Long eventId) {
        log.info("Совершаем поиск события с ID = {}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() ->
                new DataNotFoundException(String.format("Событие с ID = %d не найдено", eventId)));

    }
}

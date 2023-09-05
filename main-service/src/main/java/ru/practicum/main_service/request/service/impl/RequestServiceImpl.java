package ru.practicum.main_service.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.error.exception.ConflictException;
import ru.practicum.main_service.error.exception.DataNotFoundException;
import ru.practicum.main_service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main_service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.enums.EventState;
import ru.practicum.main_service.event.model.enums.RequestStatusAction;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.event.service.StatsService;
import ru.practicum.main_service.request.dto.RequestDto;
import ru.practicum.main_service.request.mapper.RequestMapper;
import ru.practicum.main_service.request.model.Request;
import ru.practicum.main_service.request.model.RequestStatus;
import ru.practicum.main_service.request.repository.RequestRepository;
import ru.practicum.main_service.request.service.RequestService;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final StatsService statsService;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public RequestDto saveRequest(Long userId, Long eventId) {
        log.info("METHOD: SAVE_REQUEST");
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        checkEvent(event, userId);
        Request newRequest = Request.builder().event(event).requester(user).created(LocalDateTime.now()).build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }
        log.info("Сохраняем запрос на участие в событии с ID = {}", eventId);
        return RequestMapper.toRequestDto(requestRepository.save(newRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> findRequestsByRequesterId(Long userId) {
        log.info("METHOD: FIND_REQUESTS_BY_REQUESTER_ID");
        checkUser(userId);
        log.info("Возвращаем все запросы пользователя с ID = {}", userId);
        return RequestMapper.toRequestsDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        checkUser(userId);
        log.info("Отменяем запрос");
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("Пользователь с ID = %d не найден", requestId)));
        checkUserIsOwner(request.getRequester().getId(), userId);
        request.setStatus(RequestStatus.CANCELED);
        log.info("Запрос отменен. Статус запроса успешно изменен. Сохраняем изменения...");
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> findRequestsByEventOwner(Long userId, Long eventId) {
        checkUser(userId);
        Event event = checkEvent(eventId);
        checkUserIsOwner(event.getInitiator().getId(), userId);
        log.info("Возвращаем запросы на участие от инициатора с ID = {} события с ID = {}", userId, eventId);
        return RequestMapper.toRequestsDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsByEventOwner(Long userId, Long eventId,
                                                                     EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        checkUser(userId);
        Event event = checkEvent(eventId);
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();

        checkUserIsOwner(event.getInitiator().getId(), userId);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0 || requestIds.isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        List<Request> requests = requestRepository.findAllByIdIn(requestIds);

        if (requests.size() != requestIds.size()) {
            throw new DataNotFoundException("Запрос не найден");
        }
        if (!requests.stream().map(Request::getStatus).allMatch(RequestStatus.PENDING::equals)) {
            throw new ConflictException("Нельзя подтвердить запрос");
        }

        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejectedList = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatusAction.REJECTED)) {
            rejectedList.addAll(changeStatusAndSave(requests, RequestStatus.REJECTED));
        } else {
            Long newConfirmedRequests = statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) +
                    requestIds.size();
            checkEventLimit(newConfirmedRequests, event.getParticipantLimit());
            confirmedList.addAll(changeStatusAndSave(requests, RequestStatus.CONFIRMED));
            if (newConfirmedRequests >= event.getParticipantLimit()) {
                rejectedList.addAll(changeStatusAndSave(
                        requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING), RequestStatus.REJECTED)
                );
            }
        }
        log.info("Возвращаем измененные запросы на участие от инициатора с ID = {} события с ID = {}", userId, eventId);
        return new EventRequestStatusUpdateResult(RequestMapper.toRequestsDto(confirmedList), RequestMapper.toRequestsDto(rejectedList));
    }

    private void checkEvent(Event event, Long userId) {
        log.info("Валидация события с ID = {} и пользователя с ID = {}", event.getId(), userId);
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException(String.format(
                    "Пользователь c ID = %d  не может принять участие в событии, где он является инициатором", userId));
        }
        log.info("Пользователь с ID = {} может принять участие в событие с ID = {}", userId, event.getId());
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format(
                    "Событие с ID = %d должно быть сначала опубликованно", event.getId()));
        }
        log.info("Событие с ID = {} является опубликованным", event.getId());
        Optional<Request> oldRequest = requestRepository.findByEventIdAndRequesterId(event.getId(), userId);
        if (oldRequest.isPresent()) {
            throw new ConflictException("Повторный запрос");
        }
        checkEventLimit(statsService.getConfirmedRequests(List.of(event)).getOrDefault(event.getId(),
                        0L) + 1,
                event.getParticipantLimit()
        );
        log.info("Запрос успешно прошел валидацию");
    }

    private List<Request> changeStatusAndSave(List<Request> requests, RequestStatus status) {
        log.info("Изменяем статус запроса");
        requests.forEach(request -> request.setStatus(status));
        log.info("Статус запроса успешно изменен. Сохраняем измененный статус запроса");
        return requestRepository.saveAll(requests);
    }

    private Event checkEvent(Long eventId) {
        log.info("Совершаем поиск события с ID = {}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() ->
                new DataNotFoundException(String.format("Событие с ID = %d не найдено", eventId)));

    }

    private void checkEventLimit(Long newLimit, Integer eventLimit) {
        log.info("Проверяем не превышен ли лимит участников в событии");
        if (eventLimit != 0 && (newLimit > eventLimit)) {
            throw new ConflictException("Лимит участников превышен");
        }
        log.info("Лимит участников не привышен");
    }

    private void checkUserIsOwner(Long initiatorId, Long userId) {
        log.info("Выясняем - является ли пользователь с ID = {} инициатором события", userId);
        if (!Objects.equals(initiatorId, userId)) {
            throw new ConflictException(String.format(
                    "Пользователь с ID = %d не является инициатором события", userId));
        }
        log.info("Пользователь с ID = {} является инициатором события", userId);

    }

    private User checkUser(Long userId) {
        log.info("Совершаем поиск пользователя с ID = {}", userId);
        return userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException(String.format("Пользователь с ID = %d не найден", userId)));
    }
}

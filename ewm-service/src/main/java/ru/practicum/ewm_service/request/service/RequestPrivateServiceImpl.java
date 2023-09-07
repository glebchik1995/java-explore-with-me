package ru.practicum.ewm_service.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.repository.EventRepository;
import ru.practicum.ewm_service.exception.exception.ConflictException;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm_service.request.mapper.RequestMapper;
import ru.practicum.ewm_service.request.model.ParticipationRequest;
import ru.practicum.ewm_service.request.repository.RequestRepository;
import ru.practicum.ewm_service.user.model.User;
import ru.practicum.ewm_service.user.repository.UserRepository;
import ru.practicum.ewm_service.util.enums.State;
import ru.practicum.ewm_service.util.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm_service.util.enums.Status.CONFIRMED;
import static ru.practicum.ewm_service.util.enums.Status.PENDING;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RequestPrivateServiceImpl implements RequestPrivateService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException(String.format(
                        "Событие с ID = %d не найдено", eventId)));

        User user = checkUser(userId);

        if (event.getInitiator().equals(user)) {
            throw new ConflictException(String.format(
                    "Пользователь c ID = %d  не может принять участие в событии c ID = %d, где он является инициатором",
                    userId, eventId));
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException(String.format(
                    "Событие с ID = %d должно быть сначала опубликованно", event.getId()));
        }
        if (requestRepository.existsByRequesterAndEvent(user, event)) {
            throw new ConflictException("Невозможно отправить повторный запрос");
        }
        Long confirmedRequests = requestRepository.findConfirmedRequests(eventId);
        if (event.getParticipantLimit() > 0 && Objects.equals(event.getParticipantLimit(), confirmedRequests)) {
            throw new ConflictException("У события достигнут лимит запросов на участие");
        }
        log.info("Запрос успешно прошел валидацию");
        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .build();
        if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
            request.setStatus(PENDING);
        } else {
            request.setStatus(CONFIRMED);
        }
        log.info("Сохраняем запрос на участие в событии с ID = {}", eventId);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsOfUser(Long userId) {
        User user = checkUser(userId);
        log.info("Возвращаем все запросы пользователя с ID = {}", userId);
        return requestRepository.findAllByRequester(user).stream()
                .map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = checkUser(userId);
        log.info("Отменяем запрос");
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Запроса с ID = %d не найден", requestId)));
        if (!request.getRequester().equals(user)) {
            throw new IllegalArgumentException("Запрос не принадлежит пользователю");
        }
        request.setStatus(Status.CANCELED);
        log.info("Запрос отменен. Статус запроса успешно изменен. Сохраняем изменения...");
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    private User checkUser(Long userId) {
        log.info("Совершаем поиск пользователя с ID = {}", userId);
        return userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException(String.format("Пользователь с ID = %d не найден", userId)));
    }
}

package ru.practicum.ewm_service.request.service;

import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestPrivateService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsOfUser(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}

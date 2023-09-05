package ru.practicum.main_service.request.service;

import ru.practicum.main_service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main_service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.main_service.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> findRequestsByRequesterId(Long userId);

    RequestDto saveRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> findRequestsByEventOwner(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsByEventOwner(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
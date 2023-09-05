package ru.practicum.main_service.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.request.dto.RequestDto;
import ru.practicum.main_service.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Slf4j
public class RequestPrivateController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        log.info("POST запрос на создание пользователем с ID= {} запроса на участие в событии с ID = {}",
                userId, eventId);
        return requestService.saveRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> findRequestsByRequesterId(@PathVariable Long userId) {
        log.info("GET запрос на получение всех запросов на участие в событиях пользователя с ID = {}", userId);
        return requestService.findRequestsByRequesterId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        log.info("PATCH запрос на отмену запроса пользователем с ID = {}", userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
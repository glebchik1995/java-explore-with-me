package ru.practicum.main_service.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main_service.request.dto.RequestDto;
import ru.practicum.main_service.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {

    public RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus().name())
                .build();
    }

    public List<RequestDto> toRequestsDto(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}

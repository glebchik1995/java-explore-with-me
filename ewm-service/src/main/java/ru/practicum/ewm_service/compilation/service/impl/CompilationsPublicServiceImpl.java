package ru.practicum.ewm_service.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.client.Client;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.mapper.CompilationMapper;
import ru.practicum.ewm_service.compilation.model.Compilation;
import ru.practicum.ewm_service.compilation.repository.CompilationRepository;
import ru.practicum.ewm_service.compilation.service.CompilationsPublicService;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.mapper.EventMapper;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;
import ru.practicum.ewm_service.request.repository.RequestRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CompilationsPublicServiceImpl implements CompilationsPublicService {

    private final Client statClient;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, page).stream()
                    .map(compilation ->
                            CompilationMapper.toCompilationDto(compilation, getEventShortDto(compilation.getEvents())))
                    .collect(Collectors.toList());
        }
        log.info("Получаем список всех сборников");
        return compilationRepository.findAll(page).stream()
                .map(compilation ->
                        CompilationMapper.toCompilationDto(compilation, getEventShortDto(compilation.getEvents())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Совершаем поиск сборника с ID = {}", compId);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException(String.format(
                        "Подборка с ID = %d не найдена", compId)));
        return CompilationMapper.toCompilationDto(compilation, getEventShortDto(compilation.getEvents()));
    }

    private List<EventShortDto> getEventShortDto(List<Event> events) {
        List<EventShortDto> eventDtoShort = events.stream()
                .map(EventMapper::toEventDtoShort)
                .collect(Collectors.toList());
        eventDtoShort.forEach(e -> e.setConfirmedRequests(requestRepository.findConfirmedRequests(e.getId())));
        eventDtoShort.forEach(e -> e.setViews(statClient.getView(e.getId())));
        return eventDtoShort;

    }
}

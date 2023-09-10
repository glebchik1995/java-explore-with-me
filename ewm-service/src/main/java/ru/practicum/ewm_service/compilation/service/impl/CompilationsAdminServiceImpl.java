package ru.practicum.ewm_service.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm_service.client.Client;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.ewm_service.compilation.mapper.CompilationMapper;
import ru.practicum.ewm_service.compilation.model.Compilation;
import ru.practicum.ewm_service.compilation.repository.CompilationRepository;
import ru.practicum.ewm_service.compilation.service.CompilationsAdminService;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.mapper.EventMapper;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.repository.EventRepository;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;
import ru.practicum.ewm_service.request.repository.RequestRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompilationsAdminServiceImpl implements CompilationsAdminService {

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final Client client;

    @Override
    public CompilationDto createCompilations(NewCompilationDto newCompilationDto) {

        log.info("Сохраняем новую подборку");
        List<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        Compilation compilation = compilationRepository.save(
                CompilationMapper.toCompilationModel(newCompilationDto, events
                ));

        return CompilationMapper.toCompilationDto(compilation, getEventShortDto(events));
    }

    @Override
    public CompilationDto updateCompilationById(Long compId, UpdateCompilationRequestDto updateCompilation) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(()
                -> new DataNotFoundException(String.format("Подборка с ID = %d не найдена", compId)));
        if (!CollectionUtils.isEmpty(updateCompilation.getEvents())) {
            List<Event> events = eventRepository.findByIdIn(updateCompilation.getEvents());
            compilation.setEvents(events);
        }
        List<Event> events = compilation.getEvents();
        Optional.ofNullable(updateCompilation.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilation.getPinned()).ifPresent(compilation::setPinned);
        Optional.ofNullable(updateCompilation.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilation.getPinned()).ifPresent(compilation::setPinned);
        log.info("Подборка с ID = {} изменена", compId);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), getEventShortDto(events));
    }

    @Override
    public void deleteCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException(String.format(
                        "Подборка с ID = %d не найдена", compId)));
        compilationRepository.delete(compilation);
    }

    private List<EventShortDto> getEventShortDto(List<Event> events) {
        List<EventShortDto> eventShortDto = events.stream()
                .map(EventMapper::toEventDtoShort)
                .collect(Collectors.toList());
        eventShortDto.forEach(e -> e.setConfirmedRequests(requestRepository.findConfirmedRequests(e.getId())));
        eventShortDto.forEach(e -> e.setViews(client.getView(e.getId())));
        return eventShortDto;
    }
}

package ru.practicum.main_service.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.main_service.compilation.mapper.CompilationMapper;
import ru.practicum.main_service.compilation.model.Compilation;
import ru.practicum.main_service.compilation.repository.CompilationRepository;
import ru.practicum.main_service.compilation.service.CompilationService;
import ru.practicum.main_service.error.exception.DataNotFoundException;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.service.EventService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final EventService eventService;
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        log.info("METHOD: SAVE_COMPILATION");
        List<Event> events = findEventsFromNewCompilationDto(newCompilationDto);
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilationModel(newCompilationDto, events));
        log.info("Сохраняем новый сборник с именем");
        return findCompilationById(compilation.getId());
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        log.info("Совершаем поиск сборника с ID = {}", compId);
        Compilation compilation = checkCompilation(compId);
        List<EventShortDto> eventsShortDto = eventService.toEventsShortDto(compilation.getEvents());
        return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
    }

    @Override
    public List<CompilationDto> findAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Получаем список всех сборников");
        List<Compilation> compilations = findCompilations(pinned, from, size);
        Map<Long, EventShortDto> eventsShortDto = findEventsShortDto(compilations);
        return compilations.stream()
                .map(compilation -> {
                    List<EventShortDto> compEventsShortDto = new ArrayList<>();
                    compilation.getEvents().forEach(event -> compEventsShortDto.add(eventsShortDto.get(event.getId())));
                    return CompilationMapper.toCompilationDto(compilation, compEventsShortDto);
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequestDto updateCompilationRequest) {
        log.info("METHOD: UPDATE_Compilation");
        Compilation compilation = checkCompilation(compId);
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventService.getEventsByIds(updateCompilationRequest.getEvents());
            checkSize(events, updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        compilationRepository.save(compilation);
        log.info("Сборник с ID = {} обновлен", compId);
        return findCompilationById(compId);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        log.info("METHOD: DELETE_COMPILATION");
        checkCompilation(compId);
        compilationRepository.deleteById(compId);
        log.info("Сборник с ID = {} удален", compId);
    }

    private List<Compilation> findCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            return compilationRepository.findAll(pageable).toList();
        } else {
            return compilationRepository.findAllByPinned(pinned, pageable);
        }
    }

    private Map<Long, EventShortDto> findEventsShortDto(List<Compilation> compilations) {
        Set<Event> uniqueEvents = new HashSet<>();
        compilations.forEach(compilation -> uniqueEvents.addAll(compilation.getEvents()));
        Map<Long, EventShortDto> eventsShortDto = new HashMap<>();
        eventService.toEventsShortDto(new ArrayList<>(uniqueEvents)).forEach(event -> eventsShortDto.put(event.getId(), event));
        return eventsShortDto;
    }

    private List<Event> findEventsFromNewCompilationDto(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventService.getEventsByIds(newCompilationDto.getEvents());
            checkSize(events, newCompilationDto.getEvents());
            return events;
        }
        return List.of();
    }

    private Compilation checkCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException(String.format(
                        "Сборник с ID = %d не найден", compId)));
    }

    private void checkSize(List<Event> events, List<Long> eventsIdToUpdate) {
        if (events.size() != eventsIdToUpdate.size()) {
            throw new DataNotFoundException("Сборник не найден");
        }
    }
}

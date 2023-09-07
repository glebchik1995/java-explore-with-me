package ru.practicum.ewm_service.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.ewm_service.compilation.mapper.CompilationMapper;
import ru.practicum.ewm_service.compilation.model.Compilation;
import ru.practicum.ewm_service.compilation.repository.CompilationRepository;
import ru.practicum.ewm_service.compilation.service.CompilationsAdminService;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.repository.EventRepository;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompilationsAdminServiceImpl implements CompilationsAdminService {

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto createCompilations(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        log.info("Сохраняем новую подборку");
        return compilationMapper.toCompilationDto(compilationRepository.save(CompilationMapper
                .toCompilationModel(newCompilationDto, events)));
    }

    @Override
    public CompilationDto updateCompilationById(Long compId, UpdateCompilationRequestDto updateCompilation) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException(String.format(
                        "Подборка с ID = %d не найдена", compId)));
        if (!CollectionUtils.isEmpty(updateCompilation.getEvents())) {
            List<Event> events = eventRepository.findByIdIn(updateCompilation.getEvents());
            compilation.setEvents(events);
        }
        Optional.ofNullable(updateCompilation.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilation.getPinned()).ifPresent(compilation::setPinned);
        log.info("Подборка с ID = {} изменена", compId);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException(String.format(
                        "Подборка с ID = %d не найдена", compId)));
        compilationRepository.delete(compilation);
    }
}

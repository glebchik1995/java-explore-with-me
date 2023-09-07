package ru.practicum.ewm_service.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.mapper.CompilationMapper;
import ru.practicum.ewm_service.compilation.model.Compilation;
import ru.practicum.ewm_service.compilation.repository.CompilationRepository;
import ru.practicum.ewm_service.compilation.service.CompilationsPublicService;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CompilationsPublicServiceImpl implements CompilationsPublicService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, page).stream()
                    .map(compilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }
        log.info("Получаем список всех сборников");
        return compilationRepository.findAll(page).stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Совершаем поиск сборника с ID = {}", compId);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException(String.format(
                        "Подборка с ID = %d не найдена", compId)));
        return compilationMapper.toCompilationDto(compilation);
    }
}

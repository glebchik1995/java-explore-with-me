package ru.practicum.ewm_service.compilation.service;

import ru.practicum.ewm_service.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationsPublicService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}

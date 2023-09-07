package ru.practicum.ewm_service.compilation.service;

import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.dto.UpdateCompilationRequestDto;

public interface CompilationsAdminService {
    CompilationDto createCompilations(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilationById(Long compId, UpdateCompilationRequestDto updateCompilationRequest);

    void deleteCompilationById(Long compId);

}

package ru.practicum.ewm_service.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.model.Compilation;
import ru.practicum.ewm_service.event.mapper.EventMapper;
import ru.practicum.ewm_service.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class CompilationMapper {

    private final EventMapper eventMapper;

    public static Compilation toCompilationModel(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents().stream().map(eventMapper::toEventDtoShort).collect(Collectors.toList()))
                .build();
    }
}

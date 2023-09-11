package ru.practicum.ewm_service.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.model.Compilation;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.Event;

import java.util.List;

@UtilityClass
public class CompilationMapper {

    public Compilation toCompilationModel(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventsShortDto) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventsShortDto)
                .build();
    }
}

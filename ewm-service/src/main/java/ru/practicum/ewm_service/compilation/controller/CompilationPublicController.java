package ru.practicum.ewm_service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.service.CompilationsPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm_service.util.Constant.DEF_VAL_FROM;
import static ru.practicum.ewm_service.util.Constant.DEF_VAL_SIZE;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationPublicController {

    private final CompilationsPublicService compilationsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(value = "from", defaultValue = DEF_VAL_FROM) @PositiveOrZero Integer from,
                                                @RequestParam(value = "size", defaultValue = DEF_VAL_SIZE) @Positive Integer size) {
        log.info("GET запрос на получение списка всех подборок");
        return compilationsService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("GET запрос на получение подборки с ID = {}", compId);
        return compilationsService.getCompilationById(compId);
    }
}

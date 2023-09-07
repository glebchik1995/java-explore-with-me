package ru.practicum.ewm_service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.category.service.CategoryPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm_service.util.Constant.DEF_VAL_FROM;
import static ru.practicum.ewm_service.util.Constant.DEF_VAL_SIZE;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryPublicController {

    private final CategoryPublicService categoryPublicService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = DEF_VAL_FROM)
                                           @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = DEF_VAL_SIZE)
                                           @Positive Integer size) {
        log.info("GET запрос на получение списка всех категорий");
        return categoryPublicService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("GET запрос на получение категории с ID={}", catId);
        return categoryPublicService.getCategoryById(catId);
    }
}

package ru.practicum.ewm_service.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.category.mapper.CategoriesMapper;
import ru.practicum.ewm_service.category.model.Category;
import ru.practicum.ewm_service.category.repository.CategoryRepository;
import ru.practicum.ewm_service.category.service.CategoryPublicService;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryPublicServiceImpl implements CategoryPublicService {
    private final CategoryRepository repository;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        log.info("Получаем список всех категорий");
        return repository.findAll(page).stream()
                .map(CategoriesMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        log.info("Совершаем поиск категории с ID = {}", catId);
        Category answer = repository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Категория с id " + catId + " не найдена"));
        log.info("Получаем категорию с ID = {}", catId);
        return CategoriesMapper.toCategoryDto(answer);
    }
}

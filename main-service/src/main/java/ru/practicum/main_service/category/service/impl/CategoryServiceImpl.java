package ru.practicum.main_service.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.mapper.CategoryMapper;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.category.service.CategoryService;
import ru.practicum.main_service.error.exception.DataAlreadyExistException;
import ru.practicum.main_service.error.exception.DataNotFoundException;
import ru.practicum.main_service.event.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        log.info("METHOD: SAVE_CATEGORY");
        if (categoryRepository.findByName(newCategoryDto.getName()) != null) {
            throw new DataAlreadyExistException(String.format(
                    "Категория с именем: %s уже существует", newCategoryDto.getName()));
        }
        log.info("Сохраняем категорию с именем = {}", newCategoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository
                .save(CategoryMapper.fromNewCategoryDtoToCategoryModel(newCategoryDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public Category findCategoryById(Long catId) {
        log.info("Совершаем поиск категории с ID = {}", catId);
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException(String.format(
                        "Категория с ID = %d не найдена", catId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAllCategories(Pageable pageable) {
        log.info("Получаем список всех категорий");
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("METHOD: UPDATE_CATEGORY");
        findCategoryById(catId);
        if (categoryRepository.findByNameAndId(categoryDto.getName(), catId) != null) {
            throw new DataAlreadyExistException(String.format(
                    "Категория с параметрами: %d, %s уже существует", catId, categoryDto.getName()));
        }
        categoryDto.setId(catId);
        log.info("Обновляем категорию");
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategoryModel(categoryDto)));
    }

    @Override
    public void deleteCategoryById(Long catId) {
        log.info("METHOD: DELETE_CATEGORY_BY_ID");
        findCategoryById(catId);
        log.info("Удаляем категорию");
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new DataAlreadyExistException(String.format(
                    "Категория с ID = %d не удалена т.к. в ней есть события", catId));
        }
        categoryRepository.deleteById(catId);
        log.info("Категория удалена");
    }
}
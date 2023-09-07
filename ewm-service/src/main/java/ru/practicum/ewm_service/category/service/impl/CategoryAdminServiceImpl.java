package ru.practicum.ewm_service.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.category.dto.NewCategoryDto;
import ru.practicum.ewm_service.category.mapper.CategoriesMapper;
import ru.practicum.ewm_service.category.model.Category;
import ru.practicum.ewm_service.category.repository.CategoryRepository;
import ru.practicum.ewm_service.category.service.CategoryAdminService;
import ru.practicum.ewm_service.event.repository.EventRepository;
import ru.practicum.ewm_service.exception.exception.ConflictException;
import ru.practicum.ewm_service.exception.exception.DataAlreadyExistException;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminServiceImpl implements CategoryAdminService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (repository.findByName(newCategoryDto.getName()) != null) {
            throw new ConflictException(String.format(
                    "Категория с именем: %s уже существует", newCategoryDto.getName()));
        }
        log.info("Сохраняем категорию с именем = {}", newCategoryDto.getName());
        return CategoriesMapper.toCategoryDto(repository.save(CategoriesMapper.toCategoryModel(newCategoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category update = repository.findById(catId).orElseThrow(
                () -> new DataNotFoundException(String.format("Категория с ID = %d уже существует", catId)));
        if (repository.findByNameAndIdNot(categoryDto.getName(), catId) != null) {
            throw new ConflictException(String.format(
                    "Категория с параметрами: %d, %s уже существует", catId, categoryDto.getName()));
        }
        Optional.ofNullable(categoryDto.getName()).ifPresent(update::setName);
        log.info("Обновляем категорию");
        return CategoriesMapper.toCategoryDto(repository.save(update));
    }

    @Override
    public void deleteCategoryById(Long catId) {
        Category delete = repository.findById(catId).orElseThrow(
                () -> new DataNotFoundException(String.format("Категория с ID = %d уже существует", catId)));
        if (!CollectionUtils.isEmpty(eventRepository.findAllByCategoryId(catId))) {
            throw new DataAlreadyExistException(String.format(
                    "Категория с ID = %d не удалена т.к. в ней есть события", catId));
        }
        repository.delete(delete);
        log.info("Категория удалена");
    }
}

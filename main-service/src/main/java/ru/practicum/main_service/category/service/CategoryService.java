package ru.practicum.main_service.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    Category findCategoryById(Long catId);

    List<CategoryDto> findAllCategories(Pageable pageable);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    void deleteCategoryById(Long catId);


}

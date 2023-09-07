package ru.practicum.ewm_service.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.category.dto.NewCategoryDto;
import ru.practicum.ewm_service.category.model.Category;

@UtilityClass
public class CategoriesMapper {
    public Category toCategoryModel(NewCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}

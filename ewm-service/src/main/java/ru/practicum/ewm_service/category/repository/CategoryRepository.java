package ru.practicum.ewm_service.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm_service.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    Category findByNameAndIdNot(String name, Long catId);
}

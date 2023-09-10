package ru.practicum.ewm_service.compilation.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm_service.compilation.model.Compilation;

import java.util.Collection;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Collection<Compilation> findAllByPinned(Boolean pinned, PageRequest page);
}

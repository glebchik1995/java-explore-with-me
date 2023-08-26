package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value =
            "SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(h.ip))" +
                    " FROM EndpointHit h" +
                    " WHERE h.timestamp BETWEEN ?2 AND ?3" +
                    " AND (coalesce(?1, null) is null or h.uri in ?1)" +
                    " GROUP BY h.app, h.uri" +
                    " ORDER BY COUNT(h.ip) DESC"
    )
    List<ViewStatsDto> getAllByTimestampAndUriNotUnique(List<String> uris,
                                                        LocalDateTime start,
                                                        LocalDateTime end);

    @Query(value =
            "SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip))" +
                    " FROM EndpointHit h" +
                    " WHERE h.timestamp BETWEEN ?2 AND ?3" +
                    " AND (coalesce(?1, null) is null or h.uri in ?1)" +
                    " GROUP BY h.app, h.uri" +
                    " ORDER BY COUNT(DISTINCT h.ip) DESC"
    )
    List<ViewStatsDto> getAllHitsByTimestampAndUriUnique(List<String> uris,
                                                         LocalDateTime start,
                                                         LocalDateTime end);
}


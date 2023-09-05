package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value =
            "SELECT new ru.practicum.stats.dto.ViewStatsDto(eh.app, eh.uri, COUNT(eh.ip)) " +
                    "FROM EndpointHit AS eh " +
                    "WHERE eh.timestamp BETWEEN :start AND :end " +
                    "AND (coalesce(:uris, null) IS NULL OR eh.uri IN :uris) " +
                    "GROUP BY eh.app, eh.uri " +
                    "ORDER BY 3 DESC "
    )
    List<ViewStatsDto> findStats(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("uris") List<String> uris);

    @Query(value =
            "SELECT new ru.practicum.stats.dto.ViewStatsDto(eh.app, eh.uri, COUNT(distinct eh.ip)) " +
                    "FROM EndpointHit AS eh " +
                    "WHERE eh.timestamp BETWEEN :start AND :end " +
                    "AND (coalesce(:uris, null) IS NULL OR eh.uri IN :uris) " +
                    "GROUP BY eh.app, eh.uri " +
                    "ORDER BY 3 DESC ")
    List<ViewStatsDto> findUniqueStats(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("uris") List<String> uris);
}
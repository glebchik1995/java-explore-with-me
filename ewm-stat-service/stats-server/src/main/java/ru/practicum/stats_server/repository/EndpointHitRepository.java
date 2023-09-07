package ru.practicum.stats_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats_server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value =
            "Select new ru.practicum.dto.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) " +
                    "from EndpointHit as h " +
                    "WHERE h.timestamp BETWEEN :start AND :end " +
                    "AND h.uri IN (:uris) " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY count(distinct h.ip) desc")
    List<ViewStatsDto> findAllByUriDistinct(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);

    @Query(value =
            "Select new ru.practicum.dto.ViewStatsDto(h.app, h.uri, count(h.ip)) " +
                    "from EndpointHit as h " +
                    "WHERE h.timestamp BETWEEN :start AND :end " +
                    "AND h.uri IN (:uris) " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY count(h.ip) desc")
    List<ViewStatsDto> findAllByUri(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris);

    @Query(value =
            "Select new ru.practicum.dto.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) " +
                    "from EndpointHit as h " +
                    "WHERE h.timestamp BETWEEN :start AND :end " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY count(distinct h.ip) desc")
    List<ViewStatsDto> findAllDistinct(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    @Query(value =
            "Select new ru.practicum.dto.ViewStatsDto(h.app, h.uri, count(h.ip)) " +
                    "from EndpointHit as h " +
                    "WHERE h.timestamp BETWEEN :start AND :end " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY count(h.ip) desc")
    List<ViewStatsDto> findAll(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    @Query(value =
            "SELECT COUNT(DISTINCT e.ip) " +
                    "FROM EndpointHit AS e " +
                    "WHERE e.uri = :uri")
    Long countDistinctByUri(@Param("uri") String s);
}

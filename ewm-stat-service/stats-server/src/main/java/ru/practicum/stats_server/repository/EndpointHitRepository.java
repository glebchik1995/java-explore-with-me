package ru.practicum.stats_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats_server.entity.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value =
            "SELECT  new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
                    "FROM EndpointHit eh " +
                    "WHERE eh.timestamp BETWEEN ?1 AND ?2 AND eh.uri IN ?3 " +
                    "GROUP BY eh.uri, eh.app " +
                    "ORDER BY COUNT(eh.ip) DESC"
    )
    List<ViewStatsDto> getAllUniqueWhereCreatedBetweenStartAndEndAndUriInList(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query(value =
            "SELECT  new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, COUNT(eh.ip)) " +
                    "FROM EndpointHit eh " +
                    "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
                    "AND eh.uri IN ?3 " +
                    "GROUP BY eh.uri, eh.app " +
                    "ORDER BY COUNT(eh.ip) DESC"
    )
    List<ViewStatsDto> getAllWhereCreatedBetweenStartAndEndAndUriInList(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query(value =
            "SELECT new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
                    "FROM EndpointHit eh " +
                    "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
                    "GROUP BY eh.uri, eh.app " +
                    "ORDER BY COUNT(eh.ip) DESC"
    )
    List<ViewStatsDto> getAllUniqueWhereCreatedBetweenStartAndEnd(LocalDateTime start, LocalDateTime end);

    @Query(value =
            "SELECT new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, COUNT(eh.ip)) " +
                    "FROM EndpointHit eh " +
                    "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
                    "GROUP BY eh.uri, eh.app " +
                    "ORDER BY COUNT(eh.ip) DESC"
    )
    List<ViewStatsDto> getAllWhereCreatedBetweenStartAndEnd(LocalDateTime start, LocalDateTime end);

}

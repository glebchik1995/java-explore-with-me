package ru.practicum.main_service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main_service.request.dto.RequestStatsDto;
import ru.practicum.main_service.request.model.Request;
import ru.practicum.main_service.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    @Query(value =
            "SELECT new ru.practicum.main_service.request.dto.RequestStatsDto(r.event.id, count(r.id)) " +
                    "FROM Request AS r " +
                    "WHERE r.event.id IN ?1 " +
                    "AND r.status = 'CONFIRMED' " +
                    "GROUP BY r.event.id")
    List<RequestStatsDto> findConfirmedRequests(List<Long> eventsId);

    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);
}

package ru.practicum.ewm_service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.request.model.ParticipationRequest;
import ru.practicum.ewm_service.user.model.User;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequester(User user);

    boolean existsByRequesterAndEvent(User user, Event event);

    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);

    Collection<ParticipationRequest> findAllByEvent(Event event);

    @Query(value =
            "SELECT count(PR.id) FROM ParticipationRequest AS PR " +
                    "WHERE PR.event.id in :eventId " +
                    "AND PR.status = 'CONFIRMED' "
    )
    Long findConfirmedRequests(Long eventId);
}

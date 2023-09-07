package ru.practicum.ewm_service.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.user.model.User;
import ru.practicum.ewm_service.util.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participation_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Event event;

    @Column(nullable = false)
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "requester", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    User requester;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Status status;
}

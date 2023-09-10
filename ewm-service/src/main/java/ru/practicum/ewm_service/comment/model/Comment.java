package ru.practicum.ewm_service.comment.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "text", length = 200)
    String text;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;

    @Column(name = "created_date")
    LocalDateTime created;

}

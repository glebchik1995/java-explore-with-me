package ru.practicum.ewm_service.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm_service.category.mapper.CategoriesMapper;
import ru.practicum.ewm_service.category.model.Category;
import ru.practicum.ewm_service.event.dto.EventDto;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.dto.NewEventDto;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.Location;
import ru.practicum.ewm_service.user.mapper.UserMapper;
import ru.practicum.ewm_service.user.model.User;
import ru.practicum.ewm_service.util.enums.State;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public Event toEventModel(NewEventDto newEventDto, Category category, Location location,
                              User user, LocalDateTime now, State pending) {
        return Event.builder()
                .category(category)
                .location(location)
                .initiator(user)
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .createdOn(now)
                .state(pending)
                .build();
    }

    public EventDto toEventDto(Event event) {
        return EventDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoriesMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserDtoShort(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public EventShortDto toEventDtoShort(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoriesMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserDtoShort(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }
}
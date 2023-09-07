package ru.practicum.main_service.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.mapper.CategoryMapper;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.dto.NewEventDto;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.enums.EventState;
import ru.practicum.main_service.location.model.Location;
import ru.practicum.main_service.location.mapper.LocationMapper;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.model.UserMapper;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public Event toEventModel(NewEventDto newEventDto, User initiator, Category category, Location location, LocalDateTime createdOn, EventState state) {
        return Event.builder()
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .paid(newEventDto.getPaid())
                .state(state)
                .participantLimit(newEventDto.getParticipantLimit())
                .title(newEventDto.getTitle())
                .description(newEventDto.getDescription())
                .requestModeration(newEventDto.getRequestModeration())
                .location(location)
                .category(category)
                .createdOn(createdOn)
                .annotation(newEventDto.getAnnotation())
                .build();
    }


    public EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views) {
        return EventFullDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .title(event.getTitle())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .annotation(event.getAnnotation())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .views(views)
                .build();
    }

    public EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .views(views)
                .build();
    }


}
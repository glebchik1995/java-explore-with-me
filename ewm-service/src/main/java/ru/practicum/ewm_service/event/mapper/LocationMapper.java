package ru.practicum.ewm_service.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm_service.event.dto.LocationDto;
import ru.practicum.ewm_service.event.model.Location;
@UtilityClass
public class LocationMapper {

    public Location toLocationModel(LocationDto locationDto) {
        return Location.builder()
                .lon(locationDto.getLon())
                .lat(locationDto.getLat())
                .build();
    }

    public LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

}
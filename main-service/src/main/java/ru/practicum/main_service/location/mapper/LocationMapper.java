package ru.practicum.main_service.location.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main_service.location.dto.LocationDto;
import ru.practicum.main_service.location.model.Location;

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

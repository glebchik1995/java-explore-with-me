package ru.practicum.ewm_service.util.enums;

import lombok.Getter;

@Getter
public enum Sorts {
    EVENT_DATE("event_date"),
    VIEWS("views");
    private final String sort;

    Sorts(String sort) {
        this.sort = sort;
    }
}

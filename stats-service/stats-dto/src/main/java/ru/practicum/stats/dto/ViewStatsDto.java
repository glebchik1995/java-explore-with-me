package ru.practicum.stats.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ViewStatsDto {
    String app;
    String uri;
    Long hits;
}

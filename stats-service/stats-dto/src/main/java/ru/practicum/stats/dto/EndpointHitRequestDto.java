package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.stats.util.Constant.TIME_PATTERN;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EndpointHitRequestDto {

    @NotBlank
    @Size(max = 255)
    String app;

    @NotBlank
    @Size(max = 255)
    String uri;

    @NotBlank
    @Size(max = 255)
    String ip;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_PATTERN)
    LocalDateTime timestamp;
}
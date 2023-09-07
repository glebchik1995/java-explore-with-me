package ru.practicum.ewm_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static ru.practicum.ewm_service.util.Constant.DATE_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {

    String message;

    String reason;

    String status;

    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime timestamp;

}

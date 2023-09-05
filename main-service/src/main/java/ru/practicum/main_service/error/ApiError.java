package ru.practicum.main_service.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

import static ru.practicum.main_service.utils.Constants.DATE_TIME_FORMAT;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {

    private String message;
    private String reason;
    private String status;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime timestamp;

}
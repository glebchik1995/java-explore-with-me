package ru.practicum.stats_client.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException exception) {
        log.warn("Status received: 400 BAD_REQUEST. ex.Message: " + exception.getMessage());
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse createErrorResponse(Throwable exception, HttpStatus httpStatus) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .reason(String.valueOf(exception.getCause()))
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .build();
    }
}


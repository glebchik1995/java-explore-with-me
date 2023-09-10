package ru.practicum.ewm_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm_service.exception.exception.BadRequestException;
import ru.practicum.ewm_service.exception.exception.ConflictException;
import ru.practicum.ewm_service.exception.exception.DataAlreadyExistException;
import ru.practicum.ewm_service.exception.exception.DataNotFoundException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleObjectNotFoundException(final DataNotFoundException exception) {
        log.warn("Status received: 404 NOT_FOUND. ex.Message:" + exception.getMessage());
        return createErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException exception) {
        log.warn("Status received: 409 CONFLICT. ex.Message: " + exception.getMessage());
        return createErrorResponse(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCategoryIsNotEmpty(final DataAlreadyExistException exception) {
        log.warn("Status received: 409 CONFLICT. ex.Message: " + exception.getMessage());
        return createErrorResponse(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException exception) {
        log.warn("Status received: 400 BAD_REQUEST. ex.Message: " + exception.getMessage());
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    private ApiError createErrorResponse(Throwable exception, HttpStatus httpStatus) {
        return ApiError.builder()
                .message(exception.getMessage())
                .reason(String.valueOf(exception.getCause()))
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .build();
    }
}


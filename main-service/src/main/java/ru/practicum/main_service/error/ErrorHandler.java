package ru.practicum.main_service.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.main_service.error.exception.*;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final RuntimeException exception) {
        log.warn("Status received: 404 NOT_FOUND {}", exception.getMessage(), exception);
        return createErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final RuntimeException exception) {
        log.warn("Status received: 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleOtherException(final Throwable exception) {
        log.warn("Status received: 500 SERVER_ERROR {}", exception.getMessage(), exception);
        return createErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ConflictException.class, UncorrectedDataException.class, DataAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final RuntimeException exception) {
        log.warn("Status received: 409 CONFLICT {}", exception.getMessage(), exception);
        return createErrorResponse(exception, HttpStatus.CONFLICT);
    }

    private ApiError createErrorResponse(Throwable e, HttpStatus httpStatus) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(String.valueOf(e.getCause()))
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
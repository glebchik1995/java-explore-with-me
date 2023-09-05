package ru.practicum.stats.client.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ValidationException extends ResponseStatusException {

    private final String message;

    public ValidationException(HttpStatus status, String message) {
        super(status, message);
        this.message = message;
    }

}

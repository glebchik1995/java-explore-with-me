package ru.practicum.main_service.error.exception;

public class UncorrectedDataException extends RuntimeException {
    public UncorrectedDataException(String message) {
        super(message);
    }
}

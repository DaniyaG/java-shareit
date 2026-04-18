package ru.practicum.shareit.exception;

import java.time.LocalDateTime;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, LocalDateTime start, LocalDateTime end) {
    }
}

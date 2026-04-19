package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleNotFound() {
        NotFoundException e = new NotFoundException("Объект не найден");
        Map<String, String> response = handler.handleNotFound(e);
        assertEquals("Объект не найден", response.get("error"));
    }

    @Test
    void handleForbidden() {
        ForbiddenException e = new ForbiddenException("Доступ запрещен");
        Map<String, String> response = handler.handleForbidden(e);
        assertEquals("Доступ запрещен", response.get("error"));
    }

    @Test
    void handleConflict() {
        ConflictException e = new ConflictException("Конфликт данных");
        Map<String, String> response = handler.handleConflict(e);
        assertEquals("Конфликт данных", response.get("error"));
    }

    @Test
    void handleValidation() {
        ValidationException e = new ValidationException("Ошибка валидации");
        Map<String, String> response = handler.handleValidation(e);
        assertEquals("Ошибка валидации", response.get("error"));
    }

    @Test
    void handleOther() {
        Exception e = new Exception("Неизвестная ошибка");
        Map<String, String> response = handler.handleOther(e);
        assertEquals("Неизвестная ошибка", response.get("error"));
    }
}

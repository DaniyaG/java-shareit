package ru.practicum.shareit.util;

public class EmailValidator {
    public static boolean isValid(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return false;
        }
        int dotIndex = email.lastIndexOf('.');
        return dotIndex > atIndex + 1 && dotIndex < email.length() - 1;
    }
}

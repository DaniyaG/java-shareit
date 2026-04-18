package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void noArgsConstructorAndSetters_shouldCreateAndSetFields() {
        UserDto dto = new UserDto();
        assertNotNull(dto);

        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("john@example.com");

        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    void allArgsConstructor_shouldAssignAllFields() {
        UserDto dto = new UserDto(2L, "Jane Doe", "jane@example.com");

        assertEquals(2L, dto.getId());
        assertEquals("Jane Doe", dto.getName());
        assertEquals("jane@example.com", dto.getEmail());
    }

    @Test
    void equalsAndHashCode_shouldWorkForEqualAndDifferentObjects() {
        UserDto a = new UserDto(3L, "Alex", "alex@example.com");
        UserDto b = new UserDto(3L, "Alex", "alex@example.com");
        UserDto c = new UserDto(4L, "Alex", "alex@example.com");
        UserDto d = new UserDto(3L, "Alex2", "alex@example.com");
        UserDto e = new UserDto(3L, "Alex", "alex2@example.com");

        // Равные объекты
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // Разные по id
        assertNotEquals(a, c);
        // Разные по name
        assertNotEquals(a, d);
        // Разные по email
        assertNotEquals(a, e);

        // Не равен null и объекту другого типа
        assertNotEquals(a, null);
        assertNotEquals(a, "string");

        // Два пустых DTO равны (все поля null)
        UserDto empty1 = new UserDto();
        UserDto empty2 = new UserDto();
        assertEquals(empty1, empty2);
        assertEquals(empty1.hashCode(), empty2.hashCode());
    }

    @Test
    void toString_shouldContainFieldValues() {
        UserDto dto = new UserDto(5L, "Bob", "bob@example.com");
        String s = dto.toString();

        // Lombok @Data toString обычно формата: UserDto(id=5, name=Bob, email=bob@example.com)
        assertTrue(s.contains("id=5"));
        assertTrue(s.contains("name=Bob"));
        assertTrue(s.contains("email=bob@example.com"));
    }
}

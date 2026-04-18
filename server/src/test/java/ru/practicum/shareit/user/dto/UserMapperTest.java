package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void defaultConstructor_shouldBeCovered() {
        UserMapper mapper = new UserMapper();
        assertNotNull(mapper);
    }

    @Test
    void toUserDto_nullInput_shouldReturnNull() {
        assertNull(UserMapper.toUserDto(null));
    }

    @Test
    void toUser_nullInput_shouldReturnNull() {
        assertNull(UserMapper.toUser(null));
    }

    @Test
    void toUserDto_shouldMapAllFields() {
        User user = new User(10L, "Alice", "alice@example.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@example.com", dto.getEmail());
    }

    @Test
    void toUser_shouldMapAllFields() {
        UserDto dto = new UserDto(11L, "Mike", "mike@example.com");

        User user = UserMapper.toUser(dto);

        assertNotNull(user);
        assertEquals(11L, user.getId());
        assertEquals("Mike", user.getName());
        assertEquals("mike@example.com", user.getEmail());
    }

    @Test
    void toUserDto_withNullFields_shouldMapNulls() {
        User user = new User(null, null, null);

        UserDto dto = UserMapper.toUserDto(user);

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
    }

    @Test
    void toUser_withNullFields_shouldMapNulls() {
        UserDto dto = new UserDto(null, null, null);

        User user = UserMapper.toUser(dto);

        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
    }

    @Test
    void roundTrip_entityToDtoToEntity_shouldKeepValues() {
        User original = new User(20L, "Round", "trip@example.com");

        UserDto dto = UserMapper.toUserDto(original);
        User mappedBack = UserMapper.toUser(dto);

        assertEquals(original.getId(), mappedBack.getId());
        assertEquals(original.getName(), mappedBack.getName());
        assertEquals(original.getEmail(), mappedBack.getEmail());
    }

    @Test
    void roundTrip_dtoToEntityToDto_shouldKeepValues() {
        UserDto original = new UserDto(21L, "Cycle", "cycle@example.com");

        User user = UserMapper.toUser(original);
        UserDto mappedBack = UserMapper.toUserDto(user);

        assertEquals(original.getId(), mappedBack.getId());
        assertEquals(original.getName(), mappedBack.getName());
        assertEquals(original.getEmail(), mappedBack.getEmail());
    }

    @Test
    void toUserDto_withEmptyStrings_shouldMapAsIs() {
        User user = new User(30L, "", "");

        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(30L, dto.getId());
        assertEquals("", dto.getName());
        assertEquals("", dto.getEmail());
    }

    @Test
    void toUser_withEmptyStrings_shouldMapAsIs() {
        UserDto dto = new UserDto(31L, "", "");

        User user = UserMapper.toUser(dto);

        assertEquals(31L, user.getId());
        assertEquals("", user.getName());
        assertEquals("", user.getEmail());
    }
}

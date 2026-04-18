package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Ivan", "ivan@mail.ru");
        userDto = new UserDto(1L, "Ivan", "ivan@mail.ru");
    }

    @Test
    void create_whenValid_thenSaveUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository).save(any());
    }

    @Test
    void create_whenEmailExists_thenThrowConflictException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_whenNameChanged_thenUpdateOnlyName() {
        UserDto updateDto = new UserDto(null, "NewName", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.update(1L, updateDto);

        assertEquals("NewName", result.getName());
        assertEquals("ivan@mail.ru", result.getEmail());
    }

    @Test
    void update_whenEmailExistsForOtherUser_thenThrowConflictException() {
        UserDto updateDto = new UserDto(null, null, "other@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void getById_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void getAll_thenReturnCollection() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        Collection<UserDto> result = userService.getAll();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void delete_whenUserExists_thenInvokeDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void update_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(1L, userDto));
    }

    @Test
    void getUserEntityById_whenNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserEntityById(1L));
    }

    @Test
    void delete_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.delete(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void getById_whenNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void create_whenEmailIsNull_thenThrowValidationException() {
        userDto.setEmail(null);
        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void create_whenEmailIsBlank_thenThrowValidationException() {
        userDto.setEmail("  ");
        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void getById_whenUserExists_thenReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getName());
    }

    @Test
    void update_whenNameIsBlank_thenDoNotUpdateName() {
        UserDto updateDto = new UserDto(null, "  ", "new@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.update(1L, updateDto);

        assertEquals("Ivan", result.getName());
        assertEquals("new@mail.ru", result.getEmail());
    }

    @Test
    void update_whenNameIsNull_thenDoNotUpdateName() {
        UserDto updateDto = new UserDto(null, null, "new@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.update(1L, updateDto);

        assertEquals("Ivan", result.getName());
    }

    @Test
    void update_whenEmailIsNull_thenDoNotUpdateEmail() {
        UserDto updateDto = new UserDto(null, "NewName", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.update(1L, updateDto);

        assertEquals("ivan@mail.ru", result.getEmail());
    }

}


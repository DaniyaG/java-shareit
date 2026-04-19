package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User user;
    private ItemRequest request;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Requestor", "req@mail.ru");
        requestDto = new ItemRequestDto("Нужна стремянка");
        request = new ItemRequest();
        request.setId(1L);
        request.setDescription(requestDto.getDescription());
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
    }

    @Test
    void create_whenValid_thenSaveRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(request);

        ItemRequestOutDto result = requestService.create(1L, requestDto);

        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
        verify(requestRepository).save(any());
    }

    @Test
    void create_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.create(1L, requestDto));
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getUserRequests_whenValid_thenReturnList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of());

        List<ItemRequestOutDto> result = requestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
    }

    @Test
    void getRequestById_whenValid_thenReturnDtoWithItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        User itemOwner = new User(10L, "Owner", "owner@mail.ru");

        Item item = new Item(1L, "Стремянка", "10 ступеней", true, itemOwner, 1L);

        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestOutDto result = requestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Стремянка", result.getItems().get(0).getName());
    }

    @Test
    void getAllRequests_whenValid_thenReturnOthersRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of());

        List<ItemRequestOutDto> result = requestService.getAllRequests(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(requestRepository).findAllByRequestorIdNotOrderByCreatedDesc(1L);
    }

    @Test
    void getUserRequests_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getUserRequests(1L));
    }

    @Test
    void getAllRequests_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getAllRequests(1L));
    }

    @Test
    void getRequestById_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, 1L));
    }

    @Test
    void getRequestById_whenRequestNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, 1L));
    }

    @Test
    void getUserRequests_whenListEmpty_thenReturnEmptyList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(Collections.emptyList());
        List<ItemRequestOutDto> result = requestService.getUserRequests(1L);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).findAllByRequestIdIn(anyList());
    }
}

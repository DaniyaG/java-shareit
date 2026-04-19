package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User author;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@mail.ru");
        author = new User(2L, "Author", "author@mail.ru");
        item = new Item(1L, "Дрель", "Мощная", true, owner, null);
        itemDto = new ItemDto(1L, "Дрель", "Мощная", true, null);
    }

    @Test
    void create_whenValid_thenSaveItem() {
        when(userService.getUserEntityById(anyLong())).thenReturn(owner);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.create(1L, itemDto);

        assertNotNull(result);
        assertEquals("Дрель", result.getName());
        verify(itemRepository).save(any());
    }

    @Test
    void update_whenNotOwner_thenThrowForbidden() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(ForbiddenException.class,
                () -> itemService.update(2L, 1L, itemDto));
    }

    @Test
    void getById_whenOwner_thenAddBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());

        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.empty());

        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                anyLong(), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.empty());

        ItemWithBookingsDto result = itemService.getById(1L, 1L);

        assertNotNull(result);

        verify(bookingRepository, times(1))
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(1))
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(), any(), any());
    }

    @Test
    void getById_whenNotOwner_thenNoBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());

        ItemWithBookingsDto result = itemService.getById(2L, 1L);

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());

        verify(bookingRepository, never()).findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any());
    }

    @Test
    void search_whenTextIsBlank_thenReturnEmptyList() {
        List<ItemDto> result = itemService.search("  ");
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(anyString());
    }

    @Test
    void createComment_whenNoFinishedBooking_thenThrowValidationException() {
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        CommentDto commentDto = new CommentDto(null, "Текст", null, null);

        assertThrows(ValidationException.class,
                () -> itemService.createComment(2L, 1L, commentDto));
    }

    @Test
    void createComment_whenValid_thenSaveComment() {
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        when(userService.getUserEntityById(anyLong())).thenReturn(author);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Comment comment = new Comment(1L, "Супер", item, author, LocalDateTime.now());
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = itemService.createComment(2L, 1L, new CommentDto(null, "Супер", null, null));

        assertNotNull(result);
        assertEquals("Супер", result.getText());
        verify(commentRepository).save(any());
    }

    @Test
    void update_whenValid_thenUpdateAllFields() {
        ItemDto updateDto = new ItemDto(null, "Новое имя", "Новое описание", false, null);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.update(1L, 1L, updateDto);

        assertEquals("Новое имя", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertFalse(result.getAvailable());
        verify(itemRepository).save(item);
    }

    @Test
    void update_whenItemNotFound_thenThrowNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.update(1L, 1L, itemDto));
    }

    @Test
    void search_whenTextValid_thenReturnItems() {
        when(itemRepository.search(anyString())).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("дрель");

        assertFalse(result.isEmpty());
        assertEquals("Дрель", result.get(0).getName());
        verify(itemRepository).search("дрель");
    }

    @Test
    void getById_whenOwner_thenAddBookingsMapping() {
        Booking lastBooking = new Booking();
        lastBooking.setId(10L);
        lastBooking.setBooker(author);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(Optional.of(lastBooking));

        ItemWithBookingsDto result = itemService.getById(1L, 1L);

        assertNotNull(result.getLastBooking());
        assertEquals(10L, result.getLastBooking().getId());
    }

    @Test
    void getById_whenItemNotFound_thenThrowNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    }

    @Test
    void createComment_whenItemNotFound_thenThrowNotFoundException() {
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        when(userService.getUserEntityById(anyLong())).thenReturn(author);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(2L, 1L, new CommentDto(null, "Текст", null, null)));
    }

    @Test
    void update_whenFieldsAreNull_thenKeepOldValues() {
        Item existingItem = new Item(1L, "Old", "OldDesc", true, owner, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any())).thenReturn(existingItem);

        // В DTO все поля null
        ItemDto updateDto = new ItemDto();
        ItemDto result = itemService.update(1L, 1L, updateDto);

        assertEquals("Old", result.getName());
        assertEquals("OldDesc", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void getById_whenNotFound_thenThrowNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    }


}

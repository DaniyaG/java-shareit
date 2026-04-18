package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "Booker", "booker@mail.ru");
        owner = new User(2L, "Owner", "owner@mail.ru");
        item = new Item(1L, "Дрель", "Описание", true, owner, null);
        bookingRequestDto = new BookingRequestDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void approve_whenValid_thenStatusChanged() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.approve(2L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approve_whenNotOwner_thenThrowForbidden() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(ForbiddenException.class, () -> bookingService.approve(3L, 1L, true));
    }

    @Test
    void getById_whenValidUser_thenResultDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getAllByOwner_withAllStates() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        bookingService.getAllByOwner(userId, "CURRENT");
        verify(bookingRepository).findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(userId), any(), any());

        bookingService.getAllByOwner(userId, "PAST");
        verify(bookingRepository).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(userId), any());

        bookingService.getAllByOwner(userId, "FUTURE");
        verify(bookingRepository).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(eq(userId), any());

        bookingService.getAllByOwner(userId, "WAITING");
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);

        bookingService.getAllByOwner(userId, "REJECTED");
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
    }

    @Test
    void create_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, bookingRequestDto));
    }

    @Test
    void approve_whenBookingNotFound_thenThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void getById_whenBookingNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    }

    @Test
    void getAllByBooker_withAllStates() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        bookingService.getAllByBooker(userId, "CURRENT");
        verify(bookingRepository).findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(userId), any(), any());

        bookingService.getAllByBooker(userId, "PAST");
        verify(bookingRepository).findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(userId), any());

        bookingService.getAllByBooker(userId, "FUTURE");
        verify(bookingRepository).findAllByBookerIdAndStartAfterOrderByStartDesc(eq(userId), any());

        bookingService.getAllByBooker(userId, "WAITING");
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);

        bookingService.getAllByBooker(userId, "REJECTED");
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);

        bookingService.getAllByBooker(userId, "ALL");
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(userId);
    }

    @Test
    void create_whenItemNotAvailable_thenThrowValidationException() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, bookingRequestDto));
    }

    @Test
    void create_whenBookerIsOwner_thenThrowNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(2L, bookingRequestDto));
    }

    @Test
    void create_whenEndBeforeStart_thenThrowValidationException() {
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(5));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(1)); // Конец раньше начала

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, bookingRequestDto));
    }

    @Test
    void parseState_whenValid_thenStatusOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        bookingService.getAllByBooker(1L, "pAsT");
    }

    @Test
    void getById_whenUserNotOwnerOrBooker_thenThrowNotFoundException() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(new User(10L, "Other", "other@mail.ru")));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(10L, 1L));
    }

    @Test
    void getAllByOwner_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(1L, "ALL"));
    }

    @Test
    void approve_whenStatusNotWaiting_thenThrowValidationException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.approve(owner.getId(), 1L, true));
    }

    @Test
    void create_whenItemNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.create(1L, bookingRequestDto));
    }

    @Test
    void getAllByBooker_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getAllByBooker(1L, "ALL"));
    }

    @Test
    void getById_whenUserNotFoundInLambda_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    }

    @Test
    void parseState_whenUnknownState_thenThrowValidationException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        assertThrows(ValidationException.class,
                () -> bookingService.getAllByBooker(1L, "unsupported_state_name"));
    }
}


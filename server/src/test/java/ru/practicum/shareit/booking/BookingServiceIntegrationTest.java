package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void getAllByBooker() {
        UserDto owner = userService.create(new UserDto(null, "Owner", "o@mail.ru"));
        UserDto booker = userService.create(new UserDto(null, "Booker", "b@mail.ru"));
        ItemDto item = itemService.create(owner.getId(), new ItemDto(null, "Вещь", "Описание", true, null));

        BookingRequestDto bookingRequest = new BookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        bookingService.create(booker.getId(), bookingRequest);

        List<BookingDto> bookings = bookingService.getAllByBooker(booker.getId(), "ALL");

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getItem().getName(), equalTo("Вещь"));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
    }

}

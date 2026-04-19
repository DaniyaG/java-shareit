package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void create_whenValid_thenStatusOk() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(bookingClient.create(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).create(eq(1L), any());
    }

    @Test
    void create_whenStartInPast_thenStatusBadRequest() throws Exception {

        BookingRequestDto requestDto = new BookingRequestDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void getAllByBooker_whenUnknownState_thenStatusBadRequest() throws Exception {
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void getAllByBooker_whenValidState_thenStatusOk() throws Exception {
        when(bookingClient.getAllByBooker(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk());

        verify(bookingClient).getAllByBooker(1L, "FUTURE");
    }

    @Test
    void getById_thenStatusOk() throws Exception {
        when(bookingClient.getById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(bookingClient).getById(1L, 1L);
    }

    @Test
    void approve_thenStatusOk() throws Exception {
        when(bookingClient.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approve(1L, 1L, true);
    }
}

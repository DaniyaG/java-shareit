package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient requestClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void create_whenValid_thenStatusOk() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto("Нужна мощная дрель");

        when(requestClient.create(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestClient).create(1L, requestDto);
    }

    @Test
    void create_whenDescriptionIsBlank_thenStatusBadRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(""); // Пустое описание

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void getUserRequests_thenStatusOk() throws Exception {
        when(requestClient.getUserRequests(anyLong())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(requestClient).getUserRequests(1L);
    }

    @Test
    void getAllRequests_thenStatusOk() throws Exception {
        when(requestClient.getAllRequests(anyLong())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(requestClient).getAllRequests(1L);
    }

    @Test
    void getById_thenStatusOk() throws Exception {
        when(requestClient.getById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(requestClient).getById(1L, 1L);
    }

    @Test
    void create_whenNoUserHeader_thenStatusBadRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto("Описание");

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void create_whenValid_thenStatusOk() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, null);

        when(itemClient.create(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).create(1L, itemDto);
    }

    @Test
    void create_whenNameIsBlank_thenStatusBadRequest() throws Exception {

        ItemDto itemDto = new ItemDto(null, "", "Описание", true, null);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void update_thenStatusOk() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Новое название", null, null, null);

        when(itemClient.update(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).update(1L, 1L, itemDto);
    }

    @Test
    void getById_thenStatusOk() throws Exception {
        when(itemClient.getById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemClient).getById(1L, 1L);
    }

    @Test
    void search_thenStatusOk() throws Exception {
        when(itemClient.search(anyString())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk());

        verify(itemClient).search("дрель");
    }

    @Test
    void addComment_whenValid_thenStatusOk() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Отличная вещь!", null, null);

        when(itemClient.addComment(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).addComment(1L, 1L, commentDto);
    }

    @Test
    void addComment_whenTextIsBlank_thenStatusBadRequest() throws Exception {
        CommentDto commentDto = new CommentDto(null, "  ", null, null);

        mvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }
}

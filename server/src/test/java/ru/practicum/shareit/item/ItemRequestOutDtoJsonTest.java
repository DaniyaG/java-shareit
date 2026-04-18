package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestOutDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestOutDto> json;

    @Test
    void testItemRequestOutDto() throws Exception {
        ItemRequestOutDto.ItemAnswerDto answer = new ItemRequestOutDto.ItemAnswerDto(
                1L, "Дрель", 2L, 1L);

        ItemRequestOutDto dto = ItemRequestOutDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.of(2024, 10, 10, 10, 10))
                .items(List.of(answer))
                .build();

        JsonContent<ItemRequestOutDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(2);
    }
}

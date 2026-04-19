package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    String name;

    @NotBlank(message = "Описание не может быть пустым")
    String description;

    @NotNull(message = "Статус не может быть пустым")
    Boolean available;

    Long requestId;
}

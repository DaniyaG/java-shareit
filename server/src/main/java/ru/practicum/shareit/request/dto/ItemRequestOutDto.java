package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestOutDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemAnswerDto> items;

    @Data
    @AllArgsConstructor
    public static class ItemAnswerDto {
        private Long id;
        private String name;
        private Long ownerId;
        private Long requestId;
    }
}

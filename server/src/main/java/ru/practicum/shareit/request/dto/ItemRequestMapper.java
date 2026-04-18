package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto dto, User requestor) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public static ItemRequestOutDto toItemRequestOutDto(ItemRequest request, List<Item> items) {
        return ItemRequestOutDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items == null ? new ArrayList<>() : items.stream()
                        .map(ItemRequestMapper::toItemAnswerDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static ItemRequestOutDto.ItemAnswerDto toItemAnswerDto(Item item) {
        return new ItemRequestOutDto.ItemAnswerDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId(),
                item.getRequestId()
        );
    }

}

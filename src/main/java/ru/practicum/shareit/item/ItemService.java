package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemWithBookingsDto getById(Long userId, Long itemId);

    List<ItemWithBookingsDto> getAllByOwner(Long ownerId);

    List<ItemDto> search(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}

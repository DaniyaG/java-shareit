package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String ITEM_BY_ID = "/{itemId}";

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                          @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("Заголовок X-Sharer-User-Id обязателен");
        }
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(ITEM_BY_ID)
    public ItemDto update(@RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("Заголовок X-Sharer-User-Id обязателен");
        }
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping(ITEM_BY_ID)
    public ItemDto getById(@PathVariable Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        if (userId == null) {
            throw new ValidationException("Заголовок X-Sharer-User-Id обязателен");
        }
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}

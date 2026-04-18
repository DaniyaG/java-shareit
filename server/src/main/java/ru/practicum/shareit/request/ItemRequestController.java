package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestOutDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @RequestBody ItemRequestDto requestDto) {
        return requestService.create(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestOutDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getById(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

}

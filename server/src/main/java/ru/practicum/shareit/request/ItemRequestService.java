package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestOutDto create(Long userId, ItemRequestDto requestDto);

    List<ItemRequestOutDto> getUserRequests(Long userId);

    List<ItemRequestOutDto> getAllRequests(Long userId);

    ItemRequestOutDto getRequestById(Long userId, Long requestId);
}

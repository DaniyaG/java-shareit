package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestOutDto create(Long userId, ItemRequestDto dto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));

        ItemRequest request = ItemRequestMapper.toItemRequest(dto, requestor);
        return ItemRequestMapper.toItemRequestOutDto(requestRepository.save(request), null);
    }

    @Override
    public List<ItemRequestOutDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return fillWithItems(requests);
    }

    @Override
    public List<ItemRequestOutDto> getAllRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        return fillWithItems(requests);
    }

    @Override
    public ItemRequestOutDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id %d не найден", userId)));

        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return ItemRequestMapper.toItemRequestOutDto(request, items);
    }

    private List<ItemRequestOutDto> fillWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) return Collections.emptyList();

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> allItems = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequest = allItems.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        return requests.stream()
                .map(req -> ItemRequestMapper.toItemRequestOutDto(
                        req,
                        itemsByRequest.getOrDefault(req.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

}

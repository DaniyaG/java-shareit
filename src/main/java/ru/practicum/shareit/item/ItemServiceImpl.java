package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userService.getUserEntityById(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь не найдена с id %d", itemId)));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Редактировать вещь может только владелец");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemWithBookingsDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id %d не найдена", itemId)));

        ItemWithBookingsDto dto = ItemMapper.toDtoWithBookings(item);

        if (item.getOwner().getId().equals(userId)) {
            addBookings(dto);
        }

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        dto.setComments(comments);

        return dto;
    }

    @Override
    public List<ItemWithBookingsDto> getAllByOwner(Long ownerId) {
        userService.getUserEntityById(ownerId);

        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toDtoWithBookings)
                .sorted(Comparator.comparing(ItemWithBookingsDto::getId))
                .peek(this::addBookings)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        boolean canComment = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!canComment) {
            throw new ValidationException("Вы не можете оставить отзыв: аренда не завершена или не состоялась");
        }

        User author = userService.getUserEntityById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private void addBookings(ItemWithBookingsDto dto) {
        LocalDateTime now = LocalDateTime.now();

        dto.setLastBooking(bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                        dto.getId(), now, BookingStatus.APPROVED)
                .map(b -> new ItemWithBookingsDto.BookingShortDto(b.getId(), b.getBooker().getId()))
                .orElse(null));

        dto.setNextBooking(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                        dto.getId(), now, BookingStatus.APPROVED)
                .map(b -> new ItemWithBookingsDto.BookingShortDto(b.getId(), b.getBooker().getId()))
                .orElse(null));
    }

}

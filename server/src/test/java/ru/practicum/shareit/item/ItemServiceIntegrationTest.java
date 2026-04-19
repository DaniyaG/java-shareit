package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getAllByOwner() {
        UserDto ownerDto = userService.create(new UserDto(null, "Owner", "owner@mail.ru"));
        ItemDto item1 = itemService.create(ownerDto.getId(),
                new ItemDto(null, "Дрель", "Мощная", true, null));
        ItemDto item2 = itemService.create(ownerDto.getId(),
                new ItemDto(null, "Отвертка", "Крестовая", true, null));

        List<ItemWithBookingsDto> items = itemService.getAllByOwner(ownerDto.getId());

        assertThat(items, hasSize(2));
        assertThat(items.get(0).getName(), equalTo("Дрель"));
        assertThat(items.get(1).getName(), equalTo("Отвертка"));
    }
}

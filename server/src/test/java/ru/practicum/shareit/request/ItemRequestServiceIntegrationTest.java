package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {

    private final ItemRequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void getUserRequests_shouldReturnRequestsWithItems() {
        UserDto requestor = userService.create(new UserDto(null, "Requestor", "req@mail.ru"));
        UserDto owner = userService.create(new UserDto(null, "Owner", "owner@mail.ru"));

        ItemRequestDto requestDto = new ItemRequestDto("Нужен мощный перфоратор");
        ItemRequestOutDto savedRequest = requestService.create(requestor.getId(), requestDto);

        ItemDto itemDto = new ItemDto(null, "Перфоратор", "Makita 2470", true, savedRequest.getId());
        itemService.create(owner.getId(), itemDto);

        List<ItemRequestOutDto> result = requestService.getUserRequests(requestor.getId());

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDescription(), equalTo(requestDto.getDescription()));
        assertThat(result.get(0).getItems(), hasSize(1));
        assertThat(result.get(0).getItems().get(0).getName(), equalTo("Перфоратор"));
        assertThat(result.get(0).getItems().get(0).getRequestId(), equalTo(savedRequest.getId()));
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() {
        UserDto user1 = userService.create(new UserDto(null, "User1", "u1@mail.ru"));
        UserDto user2 = userService.create(new UserDto(null, "User2", "u2@mail.ru"));

        requestService.create(user1.getId(), new ItemRequestDto("Запрос от User1"));

        List<ItemRequestOutDto> result = requestService.getAllRequests(user2.getId());

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDescription(), equalTo("Запрос от User1"));
    }
}
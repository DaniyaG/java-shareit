package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {

    private final UserService userService;

    @Test
    void saveAndGetUsers() {
        UserDto userDto = new UserDto(null, "Ivan", "ivan@mail.ru");
        userService.create(userDto);

        Collection<UserDto> users = userService.getAll();

        assertThat(users, hasSize(1));
        UserDto savedUser = users.iterator().next();
        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo(userDto.getName()));
        assertThat(savedUser.getEmail(), equalTo(userDto.getEmail()));
    }
}

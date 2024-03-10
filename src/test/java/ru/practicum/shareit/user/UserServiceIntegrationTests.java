package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTests {
    private final UserServiceImpl userService;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(null, "user", "email@email.ru");
    }


    @Test
    @DisplayName("Позитивный сценарий. Создание пользователя")
    public void createUser() {
        UserDto savedUser = userService.addUser(userDto);

        assertThat(savedUser.getId(), notNullValue());
        assertEquals(userDto.getName(), savedUser.getName(), "Name не совпадают.");
        assertEquals(userDto.getEmail(), savedUser.getEmail(), "Email не совпадают.");
    }

    @Test
    @DisplayName("Негативный сценарий. Создание пользователя")
    public void createUserNegative() {
        userService.addUser(userDto);
        final Throwable e = assertThrows(Throwable.class, () -> userService.addUser(userDto));
        assertTrue(e instanceof DataIntegrityViolationException);
    }

    @Test
    @DisplayName("Получение пользователя по ID")
    public void getUser() {
        UserDto savedUser = userService.addUser(userDto);
        UserDto actualUser = userService.getUser(savedUser.getId());

        assertEquals(savedUser.getId(), actualUser.getId());
        assertEquals(savedUser.getName(), actualUser.getName());
        assertEquals(savedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    @DisplayName("Обновление пользователя с несуществующим ID")
    public void getUserNotCorrectId() {
        long userId = 2L;
        userService.addUser(userDto);

        final UserNotFoundException e =
                assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userDto));
        assertEquals("Пользователя с id " + userId + " нет в базе", e.getMessage());
    }

    @Test
    @DisplayName("Обновление пользователя с не корректным email")
    public void updateUserNotCorrectEmail() {
        userService.addUser(userDto);
        UserDto updatedUser = new UserDto(null, "NewName", "updated@email.com");
        long userId = userService.addUser(updatedUser).getId();
        String email = userDto.getEmail();
        updatedUser.setEmail(email);

        final EmailException e =
                assertThrows(EmailException.class, () -> userService.updateUser(userId, updatedUser));
        assertEquals("Email " + email + " уже существует", e.getMessage());
    }

    @Test
    @DisplayName("Получение списка пользователей")
    public void getAllUsers() {
        userService.addUser(userDto);
        UserDto updatedUser = new UserDto(null, "NewName", "com@email.com");
        userService.addUser(updatedUser);
        List<UserDto> actualUsers = userService.getAllUsers();
        assertThat(actualUsers, hasSize(2));
    }
}

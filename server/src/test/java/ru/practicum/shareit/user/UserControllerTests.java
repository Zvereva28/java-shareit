package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto userDto;


    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "user@user.com", "User");
    }

    @SneakyThrows
    @Test
    @DisplayName("Создание пользователя")
    public void createUserUserIsValidUserCreated() {

        when(userService.addUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение пользователя")
    public void getUser() {
        final long userId = 0L;

        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getUser(userId);
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение несуществующего пользователя")
    public void getUserIdNotExistReturnUserNotFoundException() {
        long userId = 100L;
        when(userService.getUser(anyLong())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение списка пользователея")
    public void getAllUsers() {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));

        verify(userService).getAllUsers();
    }

    private UserDto makeUserDto(Long id, String name, String email) {
        UserDto dto = new UserDto(id, name, email);
        return dto;
    }

    @SneakyThrows
    @Test
    @DisplayName("Обновление пользователя - только имя")
    public void updateUserUserOnlyNameUserUpdated() {
        final Long userId = 1L;
        userDto.setName("updateName");
        UserDto updatedUser = makeUserDto(userId, userDto.getName(), userDto.getEmail());
        when(userService.updateUser(anyLong(), any())).thenReturn(updatedUser);
        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));
    }

    @SneakyThrows
    @Test
    @DisplayName("Обновление пользователя - только email")
    public void updateUserUserOnlyEmailUserUpdated() {
        final Long userId = 1L;
        userDto.setEmail("update@user.com");
        UserDto updatedUser = makeUserDto(userId, userDto.getName(), userDto.getEmail());
        when(userService.updateUser(anyLong(), any())).thenReturn(updatedUser);
        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));
    }

    @SneakyThrows
    @Test
    @DisplayName("Обновление пользователя с такой же почтой")
    public void updateUserUserWithSameEmailUserUpdated() {
        final Long userId = 1L;
        userDto.setName("updateName");
        userDto.setEmail("user@user.com");
        UserDto updatedUser = makeUserDto(userId, userDto.getName(), userDto.getEmail());

        when(userService.updateUser(anyLong(), any())).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));
    }

    @SneakyThrows
    @Test
    @DisplayName("Обновление пользователя с уже занятой почтой")
    public void updateUserUserWithExistEmailEmailAlreadyExistExceptionTrows() {
        final Long userId = 1L;
        UserDto updatedUser = makeUserDto(userId, userDto.getName(), userDto.getEmail());

        when(userService.updateUser(anyLong(), any())).thenThrow(EmailException.class);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

    }
}

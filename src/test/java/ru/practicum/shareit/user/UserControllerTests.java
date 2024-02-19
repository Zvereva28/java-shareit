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
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void createUser_whenUserIsValid_thenUserCreated() {

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
    @DisplayName("Создание пользователя с пустым полем name")
    public void createUser_whenUserWithBlankName_thenReturnedBadRequest() {
        final UserDto userDtoWithBlankName = new UserDto(null, "user@user.com", null);

        when(userService.addUser(userDtoWithBlankName)).thenThrow(ValidationException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithBlankName))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
        verify(userService, never()).addUser(userDtoWithBlankName);
    }

    @SneakyThrows
    @Test
    @DisplayName("Создание пользователя с пустым полем email")
    public void createUser_whenUserWithBlankEmail_thenReturnedBadRequest() {
        final UserDto userDtoWithBlankEmail = new UserDto(null, "User", null);
        when(userService.addUser(userDtoWithBlankEmail)).thenThrow(ValidationException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithBlankEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(userDtoWithBlankEmail);
    }

    @SneakyThrows
    @Test
    @DisplayName("Создание пользователя с не корректным email")
    public void createUser_whenUserWithWrongEmail_thenReturnedBadRequest() {
        final UserDto userWithWrongEmail = new UserDto(null, "user.com", "11111");
        when(userService.addUser(userWithWrongEmail)).thenThrow(ValidationException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userWithWrongEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(userWithWrongEmail);
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
    public void getUser_whenIdNotExist_thenReturnUserNotFoundException() {
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

}

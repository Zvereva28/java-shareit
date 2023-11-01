package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private User user;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("Namer@user.com");
    }

    @Test
    @DisplayName("Создание пользователя")
    public void createUser_whenUserFieldsValid_thenSaveUser() {
        UserDto userDto = UserMapper.INSTANCE.toUserDto(user);
        when(userRepository.save(any())).thenReturn(user);

        UserDto actualUser = userService.addUser(userDto);

        assertEquals(userDto.getId(), actualUser.getId(), "Id");
        assertEquals(userDto.getName(), actualUser.getName(), "Имя");
        assertEquals(userDto.getEmail(), actualUser.getEmail(), "Email");
        verify(userRepository, times(1)).save(any());
    }


    @Test
    @DisplayName("Получение пользователя по существующему ID")
    public void getUser_whenUserIdExists_thenReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto actualUser = userService.getUser(user.getId());

        assertEquals(user.getId(), actualUser.getId(), "ID пользователя не совпадают");
        assertEquals(user.getName(), actualUser.getName(), "Имя пользователя не совпадает");
        assertEquals(user.getEmail(), actualUser.getEmail(), "Email пользователя не совпадает");
    }

    @Test
    @DisplayName("Получение пользователя по несуществующему ID")
    public void getUser_whenUserIdNotExists_thenThrowException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        final UserNotFoundException e =
                assertThrows(UserNotFoundException.class, () -> userService.getUser(user.getId()));
        assertEquals("Пользователя с id " + user.getId() + " нет в базе", e.getMessage());
    }


    @Test
    @DisplayName("Обновление пользователя с существующим ID")
    public void updateUser_whenUserIdExists_thenUpdateUser() {
        UserDto userDto = UserMapper.INSTANCE.toUserDto(user);
        userDto.setName("NewName");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto actualUser = userService.updateUser(user.getId(), userDto);

        assertEquals(userDto.getName(), actualUser.getName(), "Имя пользователя не совпадает");
        assertEquals(userDto.getEmail(), actualUser.getEmail(), "Email пользователя не совпадает");
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers_whenUsersExists_thenReturnList() {
        User user1 = new User(1L, "User1", "user1@user.com");
        User user2 = new User(2L, "User2", "user2@user.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> actualUsers = userService.getAllUsers();

        assertEquals(2, actualUsers.size(), "Количество пользователей не совпадает");
        assertEquals(user1.getName(), actualUsers.get(0).getName(), "Имя пользователя 1 не совпадает");
        assertEquals(user1.getEmail(), actualUsers.get(0).getEmail(), "Email пользователя 1 не совпадает");
        assertEquals(user2.getName(), actualUsers.get(1).getName(), "Имя пользователя 2 не совпадает");
        assertEquals(user2.getEmail(), actualUsers.get(1).getEmail(), "Email пользователя 2 не совпадает");
    }

    @Test
    @DisplayName("Получение пустого списка пользователей")
    void getAllUsers_whenUsersNotExists_thenReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> actualUsers = userService.getAllUsers();

        assertTrue(actualUsers.isEmpty(), "Список пользователей не пустой");
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser_whenUserIdExists_thenDeleteUser() {
        long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }


}

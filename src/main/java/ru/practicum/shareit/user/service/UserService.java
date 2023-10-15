package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto newUser);

    UserDto updateUser(long id, UserDto newUser);

    UserDto getUser(long id);

    List<UserDto> getAllUsers();

    void deleteUser(long id);

}

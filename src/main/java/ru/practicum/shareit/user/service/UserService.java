package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User addUser(User newUser);

    User updateUser(int id, User newUser);

    User getUser(int id);

    List<User> getAllUsers();

    void deleteUser(int id);

}

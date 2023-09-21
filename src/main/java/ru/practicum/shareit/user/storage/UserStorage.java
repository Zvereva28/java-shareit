package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User newUser);

    void userExist(int id);

    User updateUser(int id, User newUser);

    List<User> getAllUsers();

    User getUser(int id);

    void deleteUser(int id);
}

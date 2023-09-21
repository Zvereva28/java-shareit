package ru.practicum.shareit.user.storage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.EmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int idManager;

    @Override
    public User addUser(User user) {
        checkUniqueEmail(user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void userExist(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователя с id = " + id + " не существует");
        }
    }

    @Override
    public User updateUser(int id, User user) {
        userExist(id);
        if (Objects.nonNull(user.getName())) {
            users.get(id).setName(user.getName());
        }
        if (Objects.nonNull(user.getEmail())) {
            String email = user.getEmail();
            ArrayList<User> userArrayList = getAllUsers();
            for (User userOne : userArrayList) {
                if (userOne.getEmail().equals(email) && (userOne.getId() != id)) {
                    throw new EmailException("Пользователя с email = " + email + " уже существует");
                }
            }
            users.get(id).setEmail(user.getEmail());
        }
        return users.get(id);
    }

    @Override
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int id) {
        userExist(id);
        return users.get(id);
    }

    @Override
    public void deleteUser(int id) {
        userExist(id);
        users.remove(id);
    }

    private Integer generateId() {
        idManager++;
        return idManager;
    }

    void checkUniqueEmail(String email) {
        ArrayList<User> userArrayList = getAllUsers();
        for (User user : userArrayList) {
            if (user.getEmail().equals(email)) {
                throw new EmailException("Пользователя с email = " + email + " уже существует");
            }
        }

    }
}

package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    UserStorage userStorage;
    ItemStorage itemStorage;

    public UserServiceImpl(UserStorage userStorage, ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public User addUser(User newUser) {
        log.debug("+ createUser : {}", newUser);
        User answer = userStorage.addUser(newUser);
        log.debug("- createUser : {}", answer);

        return answer;
    }

    @Override
    public User updateUser(int id, User newUser) {
        log.debug("+ updateUser : id = {}, user = {}", id, newUser);
        User answer = userStorage.updateUser(id, newUser);
        log.debug("- updateUser : {}", answer);

        return answer;
    }

    @Override
    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("+ getAllUsers : ");
        var users = userStorage.getAllUsers();
        log.debug("- getAllUsers : {}", users);

        return users;
    }

    @Override
    public void deleteUser(int id) {
        log.debug("+ deleteUser : idUser = {}", id);
        List<Item> items = itemStorage.getAllItems(id);
        if (!items.isEmpty()) {
            Item newItem = new Item();
            newItem.setAvailable(false);
            for (Item item : items) {
                itemStorage.updateItem(id, item.getId(), newItem);
            }
        }
        userStorage.deleteUser(id);
        log.debug("- deleteUser : idUser = {}", id);
    }
}

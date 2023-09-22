package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
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
    public UserDto addUser(UserDto newUser) {
        log.info("+ createUser : {}", newUser);
        User answer = userStorage.addUser(UserDto.toUser(newUser));
        log.info("- createUser : {}", answer);

        return UserDto.toUserDto(answer);
    }

    @Override
    public UserDto updateUser(int id, UserDto newUser) {
        log.info("+ updateUser : id = {}, user = {}", id, newUser);
        User answer = userStorage.updateUser(id, UserDto.toUser(newUser));
        log.info("- updateUser : {}", answer);

        return UserDto.toUserDto(answer);
    }

    @Override
    public UserDto getUser(int id) {
        return UserDto.toUserDto(userStorage.getUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("+ getAllUsers : ");
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : userStorage.getAllUsers()) {
            usersDto.add(UserDto.toUserDto(user));
        }
        log.info("- getAllUsers : {}", usersDto);

        return usersDto;
    }

    @Override
    public void deleteUser(int id) {
        log.info("+ deleteUser : idUser = {}", id);
        List<Item> items = itemStorage.getAllItems(id);
        if (!items.isEmpty()) {
            Item newItem = new Item();
            newItem.setAvailable(false);
            for (Item item : items) {
                itemStorage.updateItem(id, item.getId(), newItem);
            }
        }
        userStorage.deleteUser(id);
        log.info("- deleteUser : idUser = {}", id);
    }
}

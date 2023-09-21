package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
class ItemServiceImpl implements ItemService {

    ItemStorage itemStorage;
    UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item addItem(int idOwner, Item item) {
        log.debug("+ addItem : idItem = {}, idUser = {}", item, idOwner);
        userStorage.userExist(idOwner);
        Item newItem = itemStorage.addItem(idOwner, item);
        log.debug("- addItem : item = {}", newItem);
        return newItem;
    }

    @Override
    public Item updateItem(int idUser, int idItem, Item item) {
        log.debug("+ updateItem : idItem = {}, idUser = {}", idItem, idUser);
        userStorage.userExist(idUser);
        Item newItem = itemStorage.updateItem(idUser, idItem, item);
        log.debug("- updateItem : {}", newItem);
        return newItem;

    }

    @Override
    public Item getItem(int id) {
        log.debug("+ getItem : idItem = {}", id);
        Item item = itemStorage.getItem(id);
        log.debug("- getItem : {}", id);
        return item;
    }

    @Override
    public List<Item> getAllItems(int idUser) {
        log.debug("+ getAllItems : idUser = {}", idUser);
        userStorage.userExist(idUser);
        var answer = itemStorage.getAllItems(idUser);
        log.debug("- getAllItems : {}", answer);
        return answer;
    }

    @Override
    public List<Item> searchItem(String text) {
        log.debug("+ searchItem : text = " + text);
        var answer = itemStorage.searchItem(text);
        log.debug("- searchItem : {}", answer);
        return itemStorage.searchItem(text);
    }
}

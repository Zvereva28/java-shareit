package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
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
    public ItemDto addItem(int idOwner, ItemDto item) {
        log.info("+ addItem : idItem = {}, idUser = {}", item, idOwner);
        userStorage.userExist(idOwner);
        Item newItem = itemStorage.addItem(idOwner, ItemDto.toItem(item));
        log.info("- addItem : item = {}", newItem);

        return ItemDto.toItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(int idUser, int idItem, ItemDto item) {
        log.info("+ updateItem : idItem = {}, idUser = {}", idItem, idUser);
        userStorage.userExist(idUser);
        Item newItem = itemStorage.updateItem(idUser, idItem, ItemDto.toItem(item));
        log.info("- updateItem : {}", newItem);

        return ItemDto.toItemDto(newItem);

    }

    @Override
    public ItemDto getItem(int id) {
        log.info("+ getItem : idItem = {}", id);
        Item item = itemStorage.getItem(id);
        log.info("- getItem : {}", id);

        return ItemDto.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems(int idUser) {
        log.info("+ getAllItems : idUser = {}", idUser);
        List<ItemDto> itemsDto = new ArrayList<>();
        userStorage.userExist(idUser);
        for (Item item : itemStorage.getAllItems(idUser)) {
            itemsDto.add(ItemDto.toItemDto(item));
        }
        log.info("- getAllItems : {}", itemsDto);

        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("+ searchItem : text = " + text);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemStorage.searchItem(text)) {
            itemsDto.add(ItemDto.toItemDto(item));
        }
        log.info("- searchItem : {}", itemsDto);

        return itemsDto;
    }
}

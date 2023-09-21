package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(int idOwner, Item item);

    Item updateItem(int idUser, int idItem, Item item);

    Item getItem(int id);

    List<Item> getAllItems(int idUser);

    List<Item> searchItem(String text);

}

package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(int idOwner, Item item);

    Item updateItem(int idOwner, int idItem, Item item);

    Item getItem(int id);

    List<Item> searchItem(String text);

    List<Item> getAllItems(int idUser);
}

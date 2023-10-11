package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(int idOwner, ItemDto item);

    ItemDto updateItem(int idUser, int idItem, ItemDto item);

    ItemDto getItem(int id);

    List<ItemDto> getAllItems(int idUser);

    List<ItemDto> searchItem(String text);

}

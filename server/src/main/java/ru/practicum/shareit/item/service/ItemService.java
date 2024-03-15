package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long idOwner, ItemDto item);

    ItemDto updateItem(long idUser, long idItem, ItemDto item);

    ItemDto getItem(long id, long idUser);

    List<ItemDto> getAllItems(long idUser, int from, int size);

    List<ItemDto> searchItem(long id, String text, int from, int size);

    Item ifItemExistReturnItem(long itemId);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);

}

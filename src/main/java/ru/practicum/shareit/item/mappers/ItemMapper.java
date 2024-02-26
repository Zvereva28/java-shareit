package ru.practicum.shareit.item.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ItemMapper {

    @Mapping(target = "requestId", source = "request.id")
    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);

    ItemOwnerDto toItemOwnerDto(Item item);
}

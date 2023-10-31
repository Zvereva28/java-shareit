package ru.practicum.shareit.item.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;


@Mapper
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "requestId", source = "request.id")
    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);

    ItemOwnerDto toItemOwnerDto(Item item);
}

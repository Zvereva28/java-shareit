package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllUserItemsRequests(long userId);

    List<ItemRequestDto> getAllItems(long userId, Integer from, Integer size);

    ItemRequestDto getItemRequest(Long userId, Long requestId);

}

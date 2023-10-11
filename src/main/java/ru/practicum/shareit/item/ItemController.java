package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.ServiceHeaders.X_USER_ID;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(value = X_USER_ID, required = true) int idOwner,
                           @RequestBody @Validated ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи ее владельцу с ID = {}", idOwner);
        return itemService.addItem(idOwner, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId, @RequestHeader(value = X_USER_ID, required = true) int idUser) {
        log.info("Получен запрос на получение вещи с ID = {}", itemId);
        return itemService.getItem(itemId, idUser);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader(value = X_USER_ID, required = true) int idUser,
                              @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на обновление вещей с ID = {}", itemId);
        return itemService.updateItem(idUser, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(value = X_USER_ID, required = true) int idUser) {
        log.info("Получен запрос на получение всех вещей владельца с ID = {}", idUser);
        return itemService.getAllItems(idUser);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam("text") String text) {
        log.info("Получен запрос на поиск вещей со следующим текстом: {}", text);
        return itemService.searchItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария");
        return itemService.postComment(userId, itemId, commentDto);
    }
}

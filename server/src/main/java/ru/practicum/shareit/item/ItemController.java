package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

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
        log.debug("Получен запрос на добавление вещи ее владельцу с ID = {}", idOwner);
        return itemService.addItem(idOwner, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId, @RequestHeader(value = X_USER_ID, required = true) int idUser) {
        log.debug("Получен запрос на получение вещи с ID = {}", itemId);
        return itemService.getItem(itemId, idUser);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader(value = X_USER_ID, required = true) int idUser,
                              @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос на обновление вещей с ID = {}", itemId);
        return itemService.updateItem(idUser, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(value = X_USER_ID, required = true) int idUser,
                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                  @RequestParam(value = "size", defaultValue = "20") Integer size) {
        log.debug("Получен запрос на получение всех вещей владельца с ID = {}", idUser);
        return itemService.getAllItems(idUser, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam("text") String text,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        log.debug("Получен запрос на поиск вещей со следующим текстом: {}", text);
        return itemService.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody CommentDto commentDto) {
        log.debug("Получен запрос на добавление комментария");
        return itemService.postComment(userId, itemId, commentDto);
    }
}

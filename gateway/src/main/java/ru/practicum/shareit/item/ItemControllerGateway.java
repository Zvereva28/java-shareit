package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemControllerGateway {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId) {
        return itemClient.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) Integer size) {
        return itemClient.getAllUserItems(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody CommentDto commentDto) {
        return itemClient.postComment(userId, itemId, commentDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam("text") String searchText,
            @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) Integer size) {
        return itemClient.searchItems(userId, searchText, from, size);
    }

}

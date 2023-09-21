package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.ServiceHeaders.X_USER_ID;


@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item addItem(@RequestHeader(value = X_USER_ID, required = true) int idOwner,
                        @RequestBody @Validated Item item) {
        return itemService.addItem(idOwner, item);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable int itemId, @RequestHeader(value = X_USER_ID, required = true) int idUser) {
        return itemService.getItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@PathVariable int itemId, @RequestHeader(value = X_USER_ID, required = true) int idUser,
                           @RequestBody Item item) {
        return itemService.updateItem(idUser, itemId, item);
    }

    @GetMapping
    public List<Item> getItems(@RequestHeader(value = X_USER_ID, required = true) int idUser) {
        return itemService.getAllItems(idUser);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam("text") String text) {
        return itemService.searchItem(text);
    }

}

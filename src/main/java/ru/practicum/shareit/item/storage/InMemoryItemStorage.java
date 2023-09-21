package ru.practicum.shareit.item.storage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private Map<Integer, Item> items = new HashMap<>();
    private int idManager;


    @Override
    public Item addItem(int idOwner, Item item) {
        item.setId(generateId());
        item.setOwner(idOwner);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(int idOwner, int idItem, Item item) {
        itemExist(idItem);

        if (idOwner != items.get(idItem).getOwner()) {
            throw new ItemNotFoundException("Предмет с id = " + idItem + " не принадлежит пользователю");
        }
        if (null != item.getName()) {
            items.get(idItem).setName(item.getName());
        }

        if (null != item.getDescription()) {
            items.get(idItem).setDescription(item.getDescription());
        }

        if (null != item.getAvailable()) {
            items.get(idItem).setAvailable(item.getAvailable());
        }

        return items.get(idItem);
    }

    @Override
    public Item getItem(int id) {
        itemExist(id);
        return items.get(id);
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Item> allItems = new ArrayList<>(items.values());
        ArrayList<Item> searchItems = new ArrayList<>();
        for (Item item : allItems) {
            if (Boolean.TRUE.equals((item.getAvailable()))
                    && (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
                    || item.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))) {
                searchItems.add(item);
            }
        }
        return searchItems;
    }

    @Override
    public List<Item> getAllItems(int idUser) {
        ArrayList<Item> allItems = new ArrayList<>(items.values());
        ArrayList<Item> userItems = new ArrayList<>();
        for (Item item : allItems) {
            if (item.getOwner() == idUser) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    private void itemExist(int id) {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException("Предмет с id = " + id + " не существует");
        }
    }

    private Integer generateId() {
        idManager++;
        return idManager;
    }
}
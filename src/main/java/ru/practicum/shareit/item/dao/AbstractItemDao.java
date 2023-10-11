package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.intterface.CrudDao;
import ru.practicum.shareit.item.model.Item;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class AbstractItemDao implements CrudDao<Item, Integer> {

    private static final String RESULT_OF_FOUND = "Found not give result!";
    private static final String RESULT_OF_UPDATE = "Update Item is failed!";
    private static final String RESULT_OF_INSERT = "Insertion Item is failed!";

    protected final DataSource dataSource;
    private final String readItem;
    private final String saveItem;
    private final String deleteItem;
    private final String updateItem;
    private final String findAllItem;

    public AbstractItemDao(DataSource dataSource, String readItem, String addItem, String deleteItem,
                           String updateItem, String findAllItem) {
        this.readItem = readItem;
        this.saveItem = addItem;
        this.deleteItem = deleteItem;
        this.updateItem = updateItem;
        this.findAllItem = findAllItem;
        this.dataSource = dataSource;
    }

    @Override
    public void save(Item item) {
    }

    @Override
    public Optional<Item> findById(Integer idItem) {
        return Optional.empty();
    }

    @Override
    public List<Item> findAll(Integer idUser) {
        return null;
    }

    @Override
    public void update(Integer idUser, Item item) {
    }

    @Override
    public void deleteById(Integer idItem) {
    }

}

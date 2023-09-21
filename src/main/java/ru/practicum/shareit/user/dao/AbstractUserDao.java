package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.intterface.CrudDao;
import ru.practicum.shareit.user.model.User;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class AbstractUserDao implements CrudDao<User, Integer> {

    private static final String RESULT_OF_FOUND = "Found not give result!";
    private static final String RESULT_OF_UPDATE = "Update User is failed!";
    private static final String RESULT_OF_INSERT = "Insertion User is failed!";

    protected final DataSource dataSource;
    private final String readUser;
    private final String saveUser;
    private final String deleteUser;
    private final String updateUser;
    private final String findAllUser;

    public AbstractUserDao(DataSource dataSource, String readUser, String addUser, String deleteUser,
                           String updateUser, String findAllUser) {
        this.readUser = readUser;
        this.saveUser = addUser;
        this.deleteUser = deleteUser;
        this.updateUser = updateUser;
        this.findAllUser = findAllUser;
        this.dataSource = dataSource;
    }

    @Override
    public void save(User entity) {

    }

    @Override
    public Optional<User> findById(Integer idUser) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll(Integer idUser) {
        return null;
    }

    @Override
    public void update(Integer idUser, User user) {
    }

    @Override
    public void deleteById(Integer idUser) {
    }
}

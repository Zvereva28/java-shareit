package ru.practicum.shareit.intterface;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudDao<E, ID> {

    void save(E entity) throws SQLException;

    Optional<E> findById(ID id) throws SQLException;

    List<E> findAll(ID id) throws SQLException;

    void update(ID id, E entity) throws SQLException;

    void deleteById(ID id) throws SQLException;

}

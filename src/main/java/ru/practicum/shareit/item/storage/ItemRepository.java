package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserIdOrderByIdAsc(long userId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.user.id = ?1 " +
            "AND LOWER(i.name) LIKE LOWER(CONCAT('%', ?2, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?2, '%')) " +
            "AND i.available = true")
    List<Item> findByUserAndNameOrDescription(Long userId, String searchText);
}
package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    private Item item;
    private Item item2;

    @BeforeEach
    void setUp() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Молоток");
        itemDto.setDescription("Деревянный");
        itemDto.setAvailable(true);
        item = itemRepository.save(ItemMapper.INSTANCE.toItem(itemDto));
        User user = userRepository.save(new User(1L, "User", "name@mail.com"));
        ItemRequest request = new ItemRequest(1L, "description", user, LocalDateTime.now());
        requestRepository.save(request);
        item.setUser(user);
        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Лестница");
        itemDto2.setDescription("металлическая");
        itemDto2.setAvailable(true);
        item2 = itemRepository.save(ItemMapper.INSTANCE.toItem(itemDto2));
        item2.setUser(user);
        item2.setRequest(request);
    }

    @Test
    @DisplayName("Получение списка вещей по ID пользователя и сорторовка по возрастанию")
    void findAllByUserIdOrderByIdAsc() {
        List<Item> items = itemRepository
                .findAllByUserIdOrderByIdAsc(1, Pageable.ofSize(2)).getContent();

        assertEquals(2, items.size());
        assertEquals(item, items.get(0));
        assertEquals(item2, items.get(1));
    }


    @Test
    @DisplayName("Получение списка вещей по ID")
    void findAllByRequestId() {
        List<Item> items = itemRepository.findAllByRequestId(1);

        assertEquals(1, items.size());
        assertEquals(item2, items.get(0));
    }


}

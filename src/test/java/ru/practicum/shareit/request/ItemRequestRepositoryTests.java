package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTests {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private User otherUser;
    private ItemRequest request;
    private ItemRequest otherRequest;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(1L, "Name", "user@ya .com"));
        otherUser = userRepository.save(new User(2L, "OtherName", "other@yandex/com"));

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Хотел бы воспользоваться щёткой для обуви");
        request = itemRequestRepository.save(ItemRequestMapper.INSTANCE.toItemRequest(requestDto));
        request.setRequestor(user);

    }

    @Test
    @DisplayName("Получение списка запросов по ID пользователя")
    void getAllByRequestorId() {
        List<ItemRequest> requests = itemRequestRepository.getAllByRequestorId(user.getId());

        assertEquals(1, requests.size());
        assertEquals(request, requests.get(0));
    }

    @Test
    @DisplayName("Получение списка запросов кроме пользователя")
    void findAllItems() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllItems(otherUser.getId(), Pageable.ofSize(2));

        assertEquals(1, requests.size());
        assertEquals(request, requests.get(0));
    }
}

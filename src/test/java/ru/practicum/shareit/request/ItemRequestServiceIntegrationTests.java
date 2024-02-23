package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIntegrationTests {

    private final ItemRequestServiceImpl requestService;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private UserDto requestorDto;
    private UserDto userDto;
    private ItemRequestDto requestDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        requestorDto = new UserDto(null, "User", "name@user.com");
        userDto = new UserDto(null, "User11", "11user@user.com");
        itemDto = new ItemDto();
        itemDto.setName("Компьютер");
        itemDto.setDescription("мощный и быстрый");
        itemDto.setAvailable(true);

        requestDto = new ItemRequestDto();
        requestDto.setDescription("Сапоги резиновые");
    }

    @Test
    @DisplayName("Создание запроса на аренду")
    void createItemRequestUserExistsItemRequestCreated() {
        User requestor = userRepository.save(UserMapper.INSTANCE.toUser(requestorDto));

        ItemRequestDto actualRequestDto = requestService.createItemRequest(requestor.getId(), requestDto);

        assertNotNull(actualRequestDto.getId());
        assertEquals(requestDto.getDescription(), actualRequestDto.getDescription());
    }

    @Test
    @DisplayName("Создание запроса на аренду, когда пользователя не существует")
    void createItemRequestUserDoesNotExistException() {

        assertThrows(UserNotFoundException.class,
                () -> requestService.createItemRequest(1L, requestDto));
    }

    @Test
    @DisplayName("Получение списка всех запросов пользователя")
    void getAllUserItemsRequestsUserExistsItemRequestsReturned() {
        User requestor = userRepository.save(UserMapper.INSTANCE.toUser(requestorDto));
        ItemRequest request = requestRepository.save(ItemRequestMapper.INSTANCE.toItemRequest(requestDto));
        request.setRequestor(requestor);
        Item item = itemRepository.save(ItemMapper.INSTANCE.toItem(itemDto));
        item.setRequest(request);
        List<ItemRequest> requests = List.of(request);

        List<ItemRequestDto> actualList = requestService.getAllUserItemsRequests(requestor.getId());

        assertEquals(requests.size(), actualList.size());
        assertEquals(requests.get(0).getDescription(), actualList.get(0).getDescription());
    }

    @Test
    @DisplayName("Получение списка запросов, когда пользователя не существует")
    void getAllUserItemsRequestsUserDoesNotExistException() {

        assertThrows(UserNotFoundException.class,
                () -> requestService.getAllUserItemsRequests(1L));
    }

    @Test
    @DisplayName("Получение списка всех запросов")
    void getAllItemsUserExistsItemRequestsReturned() {
        int from = 0;
        int size = 10;
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User requestor = userRepository.save(UserMapper.INSTANCE.toUser(requestorDto));
        ItemRequest request = requestRepository.save(ItemRequestMapper.INSTANCE.toItemRequest(requestDto));
        request.setRequestor(requestor);
        Item item = itemRepository.save(ItemMapper.INSTANCE.toItem(itemDto));
        item.setRequest(request);
        List<ItemRequest> requests = List.of(request);

        List<ItemRequestDto> actualList = requestService.getAllItems(user.getId(), from, size);

        assertEquals(requests.size(), actualList.size());
        assertEquals(requests.get(0).getDescription(), actualList.get(0).getDescription());
    }

    @Test
    @DisplayName("Получение списка запросов, когда пользователя не существует")
    void getAllItemsUserDoesNotExistException() {
        int from = 0;
        int size = 10;

        assertThrows(UserNotFoundException.class,
                () -> requestService.getAllItems(1L, from, size));
    }

    @Test
    @DisplayName("Получение запроса")
    void getItemRequestUserExistsAndRequestIdExistsItemRequestReturned() {
        User requestor = userRepository.save(UserMapper.INSTANCE.toUser(requestorDto));
        ItemRequest request = requestRepository.save(ItemRequestMapper.INSTANCE.toItemRequest(requestDto));
        request.setRequestor(requestor);
        Item item = itemRepository.save(ItemMapper.INSTANCE.toItem(itemDto));
        item.setRequest(request);

        ItemRequestDto actualRequestDto = requestService
                .getItemRequest(requestor.getId(), request.getId());

        assertEquals(request.getDescription(), actualRequestDto.getDescription());
    }

    @Test
    @DisplayName("Получение запроса, когда пользователя не существует")
    void getItemRequestUserDoesNotExisException() {

        assertThrows(UserNotFoundException.class,
                () -> requestService.getItemRequest(1L, 1L));
    }

    @Test
    @DisplayName("Получение запроса, когда запроса не существует")
    void getItemRequestRequestIdDoesNotExistException() {
        User requestor = userRepository.save(UserMapper.INSTANCE.toUser(requestorDto));

        assertThrows(ItemNotFoundException.class,
                () -> requestService.getItemRequest(requestor.getId(), 1L));
    }
}

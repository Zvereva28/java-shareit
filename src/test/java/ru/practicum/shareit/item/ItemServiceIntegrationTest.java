package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.exeption.ItemBookerException;
import ru.practicum.shareit.item.exeption.ItemException;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.logging.log4j.ThreadContext.isEmpty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.enums.BookingStatus.APPROVED;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {

    private final ItemServiceImpl itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    private ItemDto itemDto;
    private ItemDto otherItemDto;
    private UserDto userDto;
    private UserDto otherUserDto;
    private CommentDto commentDto;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "lena", "keg@mail.ru");

        otherUserDto = new UserDto(null, "2User", "2User@mail.com");

        itemDto = new ItemDto();
        itemDto.setName("Палатка");
        itemDto.setDescription("Палатка зимняя");
        itemDto.setAvailable(true);

        otherItemDto = new ItemDto();
        otherItemDto.setName("Кирка");
        otherItemDto.setDescription("золотая кирка");
        otherItemDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setText("комментарий");
        commentDto.setAuthorName("2User");

        requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен утюг");
    }

    @Test
    @DisplayName("Создание вещи")
    void createItem_thenItemIsCreated() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();

        ItemDto actualItem = itemService.addItem(userId, itemDto);

        assertEquals(itemDto.getName(),
                actualItem.getName(), "Имена не совпадают.");
        assertEquals(itemDto.getDescription(),
                actualItem.getDescription(), "Email не совпадают.");

    }

    @Test
    @DisplayName("Создание вещи на запрос")
    void createItem_whenRequest_thenItemIsCreated() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        long requestId = requestRepository.save(ItemRequestMapper.INSTANCE.toItemRequest(requestDto)).getId();
        itemDto.setRequestId(requestId);

        ItemDto actualItem = itemService.addItem(userId, itemDto);

        assertEquals(itemDto.getName(),
                actualItem.getName(), "Имена не совпадают.");
        assertEquals(itemDto.getDescription(),
                actualItem.getDescription(), "Email не совпадают.");

    }

    @Test
    @DisplayName("Создание вещи если id пользователя не существует")
    void createItem_whenUserNotExist_thenThrowUserNotFoundException() {

        final UserNotFoundException e = assertThrows(
                UserNotFoundException.class,
                () -> itemService.addItem(10, itemDto)
        );
        assertEquals("Пользователя с id 10 нет в базе", e.getMessage());
    }


    @Test
    @DisplayName("Обновление вещи")
    void updateItem_thenItemIsUpdated() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        long itemId = itemService.addItem(userId, itemDto).getId();

        ItemDto updatedItem = itemService.updateItem(userId, itemId, otherItemDto);

        assertEquals(otherItemDto.getName(),
                updatedItem.getName(), "Названия не совпадают.");
        assertEquals(otherItemDto.getDescription(),
                updatedItem.getDescription(), "Описания не совпадают.");
        assertEquals(otherItemDto.getAvailable(),
                updatedItem.getAvailable(), "Доступности не совпадают.");
    }

    @Test
    @DisplayName("Обновление вещи пользователем, который не является владельцем")
    void updateItem_whenUserExistsAndNotOwner_thenItemIsUpdated() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        long itemId = itemService.addItem(userId, itemDto).getId();
        long otherUserId = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto)).getId();

        assertThatThrownBy(() -> itemService.updateItem(otherUserId, itemId, otherItemDto))
                .isInstanceOf(ItemException.class)
                .hasMessageContaining(
                        String.format("Вещь с id 1 не принадлежит пользователю с id = 2"));
    }

    @Test
    @DisplayName("Обновление вещи, если пользователя не существует")
    void updateItem_whenUserNotExist_thenReturnUserNotFoundException() {
        long userId = 1L;
        long itemId = 1L;

        final UserNotFoundException e =
                assertThrows(UserNotFoundException.class, () -> itemService.updateItem(userId, itemId, otherItemDto));
        assertEquals("Пользователя с id " + userId + " нет в базе", e.getMessage());
    }

    @Test
    @DisplayName("Обновление вещи, если вещи не существует")
    void updateItem_whenItemNotExist_thenReturnItemNotFoundException() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        long itemId = 1L;

        final ItemNotFoundException e = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(userId, itemId, otherItemDto));
        assertEquals("Вещи с id " + itemId + " нет в базе", e.getMessage());
    }

    @Test
    @DisplayName("Обновление вещи - только available")
    void updateItemOnlyAvailable() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        long itemId = itemService.addItem(userId, itemDto).getId();
        ItemDto availableItem = new ItemDto();
        availableItem.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(userId, itemId, availableItem);

        assertEquals(itemDto.getName(),
                updatedItem.getName(), "Названия не совпадают.");
        assertEquals(itemDto.getDescription(),
                updatedItem.getDescription(), "Описания не совпадают.");
        assertEquals(availableItem.getAvailable(),
                updatedItem.getAvailable(), "Доступности не совпадают.");
    }

    @Test
    @DisplayName("Обновление вещи - только description")
    void updateItemOnlyDescription() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        long itemId = itemService.addItem(userId, itemDto).getId();
        ItemDto descriptionItem = new ItemDto();
        descriptionItem.setDescription("Аккумуляторная дрель + аккумулятор");

        ItemDto updatedItem = itemService.updateItem(userId, itemId, descriptionItem);

        assertEquals(itemDto.getName(),
                updatedItem.getName(), "Названия не совпадают.");
        assertEquals(descriptionItem.getDescription(),
                updatedItem.getDescription(), "Описания не совпадают.");
        assertEquals(itemDto.getAvailable(),
                updatedItem.getAvailable(), "Доступности не совпадают.");
    }

    @Test
    @DisplayName("Обновление вещи - только name")
    void updateItemOnlyName() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        long itemId = itemService.addItem(userId, itemDto).getId();
        ItemDto nameItem = new ItemDto();
        nameItem.setName("Аккумуляторная дрель");

        ItemDto updatedItem = itemService.updateItem(userId, itemId, nameItem);

        assertEquals(nameItem.getName(),
                updatedItem.getName(), "Названия не совпадают.");
        assertEquals(itemDto.getDescription(),
                updatedItem.getDescription(), "Описания не совпадают.");
        assertEquals(itemDto.getAvailable(),
                updatedItem.getAvailable(), "Доступности не совпадают.");
    }

    @Test
    @DisplayName("Получение вещи пользователем, который не является владельцем")
    void getItem_whenUserNotOwner_thenReturnItemOwnerDto() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        long itemId = itemService.addItem(userId, itemDto).getId();
        long otherUserId = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto)).getId();

        ItemOwnerDto actualItem = (ItemOwnerDto) itemService.getItem(itemId, otherUserId);
        actualItem.setComments(List.of(commentDto));

        assertEquals(itemDto.getName(),
                actualItem.getName(), "Названия не совпадают.");
        assertEquals(itemDto.getDescription(),
                actualItem.getDescription(), "Описания не совпадают.");
        assertEquals(itemDto.getAvailable(),
                actualItem.getAvailable(), "Доступности не совпадают.");
        assertEquals(1, actualItem.getComments().size());
    }

    @Test
    @DisplayName("Получение вещи пользователем, который является владельцем")
    void getItem_whenUserOwner_thenReturnItemOwnerDtoWithBookings() {

        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        Item item = ItemMapper.INSTANCE.toItem(itemService.addItem(userId, itemDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Comment comment = commentRepository.save(CommentMapper.INSTANCE.toComment(commentDto));
        comment.setItem(item);
        comment.setAuthor(otherUser);
        commentRepository.saveAndFlush(comment);
        List<Comment> comments = List.of(comment);

        BookingDto lastBookingDto = new BookingDto();
        lastBookingDto.setStart(LocalDateTime.now().minusDays(7));
        lastBookingDto.setEnd(LocalDateTime.now().minusDays(5));
        Booking lastBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(lastBookingDto));
        lastBooking.setBooker(otherUser);
        lastBooking.setItem(item);
        lastBooking.setStatus(APPROVED);
        bookingRepository.saveAndFlush(lastBooking);

        BookingDto nextBookingDto = new BookingDto();
        nextBookingDto.setStart(LocalDateTime.now().plusDays(5));
        nextBookingDto.setEnd(LocalDateTime.now().plusDays(7));
        Booking nextBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(nextBookingDto));
        nextBooking.setBooker(otherUser);
        nextBooking.setItem(item);
        nextBooking.setStatus(APPROVED);
        bookingRepository.saveAndFlush(nextBooking);

        ItemOwnerDto actualItem = (ItemOwnerDto) itemService.getItem(item.getId(), userId);

        assertEquals(itemDto.getName(),
                actualItem.getName(), "Названия не совпадают.");
        assertEquals(itemDto.getDescription(),
                actualItem.getDescription(), "Описания не совпадают.");
        assertEquals(itemDto.getAvailable(),
                actualItem.getAvailable(), "Доступности не совпадают.");
        assertEquals(comments.size(), actualItem.getComments().size());
        assertEquals(comments.get(0).getText(), actualItem.getComments().get(0).getText());
        assertEquals(lastBooking.getId(), actualItem.getLastBooking().getId());
        assertEquals(nextBooking.getId(), actualItem.getNextBooking().getId());

    }

    @Test
    @DisplayName("Получение списка вещей пользователя")
    void getAllUserItems() {
        int from = 0;
        int size = 10;
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        Item item = ItemMapper.INSTANCE.toItem(itemService.addItem(userId, itemDto));

        List<ItemDto> actualItems = itemService.getAllItems(userId, from, size);

        assertEquals(1, actualItems.size());
        assertEquals(item.getId(), actualItems.get(0).getId());
        assertEquals(item.getName(), actualItems.get(0).getName());
        assertEquals(item.getDescription(), actualItems.get(0).getDescription());
        assertEquals(item.getAvailable(), actualItems.get(0).getAvailable());
    }

    @Test
    @DisplayName("Поиск вещей пользователем с пустым запросом")
    void searchItems_whenUserExistsAndSearchTextIsEmpty_thenReturnEmptyList() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        itemService.addItem(userId, itemDto);
        String searchText = "";
        Integer from = 0;
        Integer size = 10;

        List<ItemDto> itemsDto = itemService.searchItem(userId, searchText, from, size);

        assertThat(itemsDto.toString(), isEmpty());
    }

    @Test
    @DisplayName("Поиск несуществующих вещей")
    void searchItems_whenUserExistsAndSearchTextIsInvalid_thenReturnEmptyList() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        itemService.addItem(userId, itemDto);
        String searchText = "Новая вещь";
        Integer from = 0;
        Integer size = 10;

        List<ItemDto> itemsDto = itemService.searchItem(userId, searchText, from, size);

        assertThat(itemsDto.toString(), isEmpty());
    }

    @Test
    @DisplayName("Добавление комментария пользователем, который не арендовал вещь")
    void postComment_whenUserIsNotBooker_thenThrowItemBookerException() {
        long userId = userRepository.save(UserMapper.INSTANCE.toUser(userDto)).getId();
        long itemId = ItemMapper.INSTANCE.toItem(itemService.addItem(userId, itemDto)).getId();
        long otherUserId = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto)).getId();

        assertThatThrownBy(() -> itemService.postComment(otherUserId, itemId, commentDto))
                .isInstanceOf(ItemBookerException.class)
                .hasMessageContaining(
                        String.format("Вещь с id %d не была арендована пользователем с id %d", itemId, otherUserId));

    }

}

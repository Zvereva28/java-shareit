package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReplyDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.mappers.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.enums.BookingStatus.WAITING;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTests {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final ItemMapperImpl itemMapper;

    private final ItemDto itemDto = new ItemDto();
    private final BookingDto bookingDto = new BookingDto();
    private final BookingDto lastBookingDto = new BookingDto();
    private final BookingDto nextBookingDto = new BookingDto();
    private UserDto userDto;
    private UserDto otherUserDto;
    private UserDto strangerDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "User1", "1user@mail.com");
        otherUserDto = new UserDto(null, "2User", "2user@mail.com");
        strangerDto = new UserDto(null, "3User", "3User@email.com");


        itemDto.setName("молот");
        itemDto.setDescription("Золотой молот");
        itemDto.setAvailable(true);

        bookingDto.setStart(LocalDateTime.now().minusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(1));
        bookingDto.setItemId(1L);
        lastBookingDto.setStart(LocalDateTime.now().minusMinutes(10));
        lastBookingDto.setEnd(LocalDateTime.now().minusMinutes(2));
        lastBookingDto.setItemId(1L);
        nextBookingDto.setStart(LocalDateTime.now().plusMinutes(2));
        nextBookingDto.setEnd(LocalDateTime.now().plusMinutes(10));
        nextBookingDto.setItemId(1L);
    }

    @Test
    @DisplayName("Создание бронирования")
    void createBookingBookingDataValidBookingCreated() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        BookingReplyDto actualBooking = (BookingReplyDto) bookingService.createBooking(otherUser.getId(), bookingDto);
        assertNotNull(actualBooking.getId());
        assertEquals(bookingDto.getStart(), actualBooking.getStart());
        assertEquals(bookingDto.getEnd(), actualBooking.getEnd());
        assertEquals(bookingDto.getItemId(), actualBooking.getItem().getId());
        assertEquals("WAITING", actualBooking.getStatus());
    }

    @Test
    @DisplayName("Создание бронирования когда дата окончания не валидна")
    void createBookingEndDateNotValidException() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        assertThrows(BookingException.class,
                () -> bookingService.createBooking(otherUser.getId(), bookingDto));
    }

    @Test
    @DisplayName("Создание бронирования на вещь, которая принадлежит пользователю")
    void createBookingBookedItemBelongToUserNotFoundException() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.createBooking(user.getId(), bookingDto));
    }

    @Test
    @DisplayName("Создание бронирования на несуществующую вещь")
    void createBookingBookedItemNotExistItemNotFoundException() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(user.getId(), bookingDto));
    }

    @Test
    @DisplayName("Создание бронирования не существующим пользователем")
    void createBookingUserNotFoundException() {
        assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDto));
    }

    @Test
    @DisplayName("Одобрение бронирования")
    void approvingBookingBookingApproved() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);

        BookingReplyDto bookingReplyDto =
                (BookingReplyDto) bookingService.approvingBooking(
                        user.getId(), booking.getId(), true);

        assertEquals("APPROVED", bookingReplyDto.getStatus());
    }

    @Test
    @DisplayName("Одобрение бронирования, когда вещь не принадлежит пользователю")
    void approvingBookingException() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(otherUser);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.approvingBooking(user.getId(), booking.getId(), true));
    }

    @Test
    @DisplayName("Одобрение бронирования - пользователя не существует")
    void approvingBookingNotExistnException() {
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.approvingBooking(1L,
                        booking.getId(), true));
    }

    @Test
    @DisplayName("Одобрение бронирования - брони не существует")
    void approvingBookingButException() {
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.approvingBooking(1L, 1L, true));
    }

    @Test
    @DisplayName("Получение бронирования")
    void getBookingBookingReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        BookingDto bookingDto =
                bookingService.getBooking(otherUser.getId(), booking.getId());

        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    @DisplayName("Получение брони, к которой пользователь не имеет доступа")
    void getBookingExistsAndUserException() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        User stranger = userRepository.save(UserMapper.INSTANCE.toUser(strangerDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setBooker(otherUser);
        booking.setItem(item);
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(stranger.getId(), booking.getId()));
    }

    @Test
    @DisplayName("Получение брони - пользователя не существует")
    void getBookingUserNotExistsException() {
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(UserMapper.INSTANCE.toUser(otherUserDto));
        booking.setItem(item);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.getBooking(1L, booking.getId()));
    }

    @Test
    @DisplayName("Получение списка  бронирований пользователя")
    void getUserAllBookingAllBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtoList = bookingService
                .getUserAllBooking(otherUser.getId(), "ALL", 0, 10);
        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка бронирований c неизвестным параметром")
    void getUserAllBookingException() {
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        assertThrows(BookingException.class,
                () -> bookingService.getUserAllBooking(otherUser.getId(), "UNKNOWN", 0, 10));
    }

    @Test
    @DisplayName("Получение списка всех бронирований с параметром без брони")
    void getUserAllBookingPastBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(lastBookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtoList = bookingService
                .getUserAllBooking(otherUser.getId(), "PAST", 0, 10);
        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований с параметром без брони")
    void getUserAllBookingStateIsFutureBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(nextBookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtoList = bookingService
                .getUserAllBooking(otherUser.getId(), "FUTURE", 0, 10);
        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований с параметром без брони")
    void getUserAllBookingStateIsCurrentBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtoList = bookingService
                .getUserAllBooking(otherUser.getId(), "CURRENT", 0, 10);
        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований с параметром без брони")
    void getUserAllBookingStateIsWaitingBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtoList = bookingService
                .getUserAllBooking(otherUser.getId(), "WAITING", 0, 10);
        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований вледельцем вещи")
    void getAllBookingByOwnerUserExistsBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingByOwner(user.getId(), "ALL", 0, 10);
        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка бронирований вледельцем вещи c неизвестным параметром")
    void getAllBookingByOwnerStateUnknownException() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));

        assertThrows(BookingException.class,
                () -> bookingService.getAllBookingByOwner(user.getId(), "UNKNOWN", 0, 10));
    }

    @Test
    @DisplayName("Получение списка всех бронирований пользователя")
    void getUserAllBookingUserExistsBookingsReturned() {
        String state = "ALL";
        int from = 0;
        int size = 10;
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);

        List<BookingDto> bookingDtoList = bookingService.getUserAllBooking(otherUser.getId(), state, from, size);

        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка бронирований вледельцем вещи c неизвестным параметром")
    void getAllBookingByOwnerStateUnknownWithException() {
        String state = "UNKNOWN";
        int from = 0;
        int size = 10;
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));

        assertThrows(BookingException.class,
                () -> bookingService.getAllBookingByOwner(user.getId(), state, from, size));
    }

    @Test
    @DisplayName("Получение списка бронирований c неизвестным параметром")
    void getUserAllBookingStateUnknownException() {
        String state = "UNKNOWN";
        int from = 0;
        int size = 10;
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));

        assertThrows(BookingException.class,
                () -> bookingService.getUserAllBooking(otherUser.getId(), state, from, size));
    }

    @Test
    @DisplayName("Получение списка всех бронирований с параметром без брони")
    void getUserAllBookingStateIsPastBookingsReturned() {
        String state = "PAST";
        int from = 0;
        int size = 10;
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(lastBookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);

        List<BookingDto> bookingDtoList = bookingService.getUserAllBooking(otherUser.getId(), state, from, size);

        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований с параметром без брони")
    void getUserAllBookingStateIsFutureBookingsParamReturned() {
        String state = "FUTURE";
        int from = 0;
        int size = 10;
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(nextBookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);

        List<BookingDto> bookingDtoList = bookingService.getUserAllBooking(otherUser.getId(), state, from, size);

        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований с параметром без брони")
    void getUserAllBookingStateIsCurrentBookingsAllReturned() {
        String state = "CURRENT";
        int from = 0;
        int size = 10;
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);

        List<BookingDto> bookingDtoList = bookingService.getUserAllBooking(otherUser.getId(), state, from, size);

        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований с параметром без брони")
    void getUserAllBookingStateIsWaitingBookingsOutReturned() {
        String state = "WAITING";
        int from = 0;
        int size = 10;
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);

        List<BookingDto> bookingDtoList = bookingService.getUserAllBooking(otherUser.getId(), state, from, size);

        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований вледельцем вещи")
    void getAllBookingByOwnerUserExistBookingsReturned() {
        String state = "ALL";
        int from = 0;
        int size = 10;
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(user.getId(), state, from, size);

        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований вледельцем вещи с параметром без брони")
    void getAllBookingByOwnerStateIsPastBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(lastBookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingByOwner(user.getId(), "PAST", 0, 10);
        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований вледельцем вещи с параметром без брони")
    void getAllBookingByOwnerStateIsFutureBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(nextBookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);

        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingByOwner(user.getId(), "FUTURE", 0, 10);

        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований вледельцем вещи с параметром без брони")
    void getAllBookingByOwnerStateIsCurrentBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);

        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingByOwner(user.getId(), "CURRENT", 0, 10);

        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований вледельцем вещи с параметром без брони")
    void getAllBookingByOwnerStateIsBookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingByOwner(user.getId(), "WAITING", 0, 10);
        assertEquals(bookings.size(), bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение списка всех бронирований вледельцем вещи с параметром без брони")
    void getAllBookingByOwnerSize2BookingsReturned() {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        User otherUser = userRepository.save(UserMapper.INSTANCE.toUser(otherUserDto));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        item.setUser(user);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        Booking lastBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(lastBookingDto));
        Booking nextBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(nextBookingDto));
        booking.setItem(item);
        lastBooking.setItem(item);
        nextBooking.setItem(item);
        booking.setBooker(otherUser);
        lastBooking.setBooker(otherUser);
        nextBooking.setBooker(otherUser);

        List<BookingDto> bookingDtoList = bookingService
                .getAllBookingByOwner(user.getId(), "ALL", 2, 1);

        assertEquals(1, bookingDtoList.size());
    }
}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReplyDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.enums.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTests {

    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;


    private UserDto userDto;
    private UserDto otherUserDto;
    private final ItemDto itemDto = new ItemDto();
    private final BookingDto bookingDto = new BookingDto();
    private final BookingDto lastBookingDto = new BookingDto();
    private final BookingDto nextBookingDto = new BookingDto();

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "user@mail.ru", "1User");
        otherUserDto = new UserDto(2L, "2user@mail.ru", "2User");

        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);

        bookingDto.setId(2L);
        bookingDto.setStart(LocalDateTime.now().minusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(1));
        bookingDto.setItemId(1L);
        lastBookingDto.setId(1L);
        lastBookingDto.setStart(LocalDateTime.now().minusMinutes(10));
        lastBookingDto.setEnd(LocalDateTime.now().minusMinutes(2));
        lastBookingDto.setItemId(1L);
        nextBookingDto.setId(3L);
        nextBookingDto.setStart(LocalDateTime.now().plusMinutes(2));
        nextBookingDto.setEnd(LocalDateTime.now().plusMinutes(10));
        nextBookingDto.setItemId(1L);
    }

    @Test
    @DisplayName("Создание бронирования на несуществующую вещь")
    void createBookingBookedItemNotExistItemNotFoundException() {
        User user = UserMapper.INSTANCE.toUser(userDto);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(NullPointerException.class,
                () -> bookingService.createBooking(user.getId(), bookingDto));
    }

    @Test
    @DisplayName("Создание бронирования на существующую вещь")
    void createBookingBookerNotExistUserNotFoundException() {
        User user = UserMapper.INSTANCE.toUser(userDto);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(user.getId(), bookingDto));
    }

    @Test
    @DisplayName("Одобрение бронирования, когда брони не существует")
    void approvingBookingBookingNotExistException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.approvingBooking(userDto.getId(),
                        bookingDto.getId(), true));
    }

    @Test
    @DisplayName("Получение бронирования")
    void getBookingBookingExistsAndUserHasAccessBookingReturned() {
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingDto);
        User user = UserMapper.INSTANCE.toUser(userDto);
        Item item = ItemMapper.INSTANCE.toItem(itemDto);
        item.setUser(user);
        booking.setItem(item);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        BookingReplyDto bookingReplyDto =
                (BookingReplyDto) bookingService.getBooking(user.getId(), booking.getId());

        assertEquals(booking.getId(), bookingReplyDto.getId());
        assertEquals(booking.getStart(), bookingReplyDto.getStart());
        assertEquals(booking.getEnd(), bookingReplyDto.getEnd());
        assertEquals(booking.getItem().getName(), bookingReplyDto.getItem().getName());
        verify(bookingRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Получение брони, к которой пользователь не имеет доступа")
    void getBookingBookingExistsAndUserHasNoAccessException() {
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingDto);
        User user = UserMapper.INSTANCE.toUser(userDto);
        Item item = ItemMapper.INSTANCE.toItem(itemDto);
        item.setUser(UserMapper.INSTANCE.toUser(otherUserDto));
        booking.setItem(item);
        booking.setBooker(UserMapper.INSTANCE.toUser(otherUserDto));

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(userDto.getId(), bookingDto.getId()));
    }

    @Test
    @DisplayName("Получение брони, когда пользователя не существует")
    void getBookingUserNotExistsException() {
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingDto);
        Item item = ItemMapper.INSTANCE.toItem(itemDto);
        item.setUser(UserMapper.INSTANCE.toUser(otherUserDto));
        booking.setItem(item);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getBooking(item.getUser().getId(), booking.getId()));
    }

    @Test
    @DisplayName("Получение брони, когда вещи не существует")
    void getBookingBookingNotExistsException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(userDto.getId(), bookingDto.getId()));
    }

    @Test
    @DisplayName("Получение списка всех бронирований пользователя")
    void getUserAllBookingUserExistsBookingsReturned() {
        String state = "ALL";
        int from = 0;
        int size = 10;
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingDto);
        User user = UserMapper.INSTANCE.toUser(userDto);
        User otherUser = UserMapper.INSTANCE.toUser(otherUserDto);
        Item item = ItemMapper.INSTANCE.toItem(itemDto);
        item.setUser(user);
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        Pageable pageable = PageRequest.of(from, size);

        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findAllByBookerIdOrderByStartDateDesc(otherUser.getId(), pageable))
                .thenReturn(new PageImpl<>(bookings, pageable, bookings.size()));

        List<BookingDto> bookingDtos = bookingService.getUserAllBooking(otherUserDto.getId(), state, from, size);

        assertEquals(bookings.size(), bookingDtos.size());
    }

    @Test
    @DisplayName("Получение списка бронирований когда пользователя не существует")
    void getUserAllBookingUserNotExistsException() {
        String state = "ALL";
        int from = 0;
        int size = 10;

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getUserAllBooking(userDto.getId(), state, from, size));
    }

    @Test
    @DisplayName("Получение списка всех бронирований владельцем вещи")
    void getAllBookingByOwnerUserExistsBookingsReturned() {
        String state = "ALL";
        int from = 0;
        int size = 10;
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingDto);
        User user = UserMapper.INSTANCE.toUser(userDto);
        User otherUser = UserMapper.INSTANCE.toUser(otherUserDto);
        Item item = ItemMapper.INSTANCE.toItem(itemDto);
        item.setUser(user);
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(WAITING);
        List<Booking> bookings = List.of(booking);
        Pageable pageable = PageRequest.of(from, size);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwnerIdOrderByStartDateDesc(user.getId(), pageable))
                .thenReturn(new PageImpl<>(bookings, pageable, bookings.size()));

        List<BookingDto> bookingDtos = bookingService.getAllBookingByOwner(user.getId(), state, from, size);

        assertEquals(bookings.size(), bookingDtos.size());
    }

    @Test
    @DisplayName("Получение списка бронирований когда пользователя не существует")
    void getAllBookingByOwnerUserNotExistsException() {
        String state = "ALL";
        int from = 0;
        int size = 10;

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllBookingByOwner(userDto.getId(), state, from, size));
    }

    @Test
    @DisplayName("Получение списка бронирований c неизвестным параметром")
    void getAllBookingByOwneStateUnknownException() {
        String state = "SOMETHING";
        int from = 0;
        int size = 10;
        User user = UserMapper.INSTANCE.toUser(userDto);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(BookingException.class,
                () -> bookingService.getAllBookingByOwner(userDto.getId(), state, from, size));
    }
}

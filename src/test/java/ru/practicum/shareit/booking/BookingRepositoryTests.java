package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.enums.BookingStatus.APPROVED;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTests {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user = new User();
    private User otherUser = new User();
    private Item item = new Item();
    private Booking booking = new Booking();
    private Booking lastBooking = new Booking();
    private Booking nextBooking = new Booking();

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(null, "User", "user@user.com");
        user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
        UserDto userDto2 = new UserDto(null, "User2", "user2@user.com");
        otherUser = userRepository.save(UserMapper.INSTANCE.toUser(userDto2));
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Молоток");
        itemDto.setDescription("Золотой молоток");
        itemDto.setAvailable(true);
        item = itemRepository
                .save(ItemMapper
                        .INSTANCE.toItem(itemDto));
        item.setUser(user);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().minusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(1));
        booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(APPROVED);
        lastBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        lastBooking.setItem(item);
        lastBooking.setBooker(otherUser);
        lastBooking.setStart(LocalDateTime.now().minusMinutes(10));
        lastBooking.setEnd(LocalDateTime.now().minusMinutes(2));
        lastBooking.setStatus(APPROVED);
        nextBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
        nextBooking.setItem(item);
        nextBooking.setBooker(otherUser);
        nextBooking.setStart(LocalDateTime.now().plusMinutes(2));
        nextBooking.setEnd(LocalDateTime.now().plusMinutes(10));
        nextBooking.setStatus(APPROVED);
    }

    @Test
    @DisplayName("Получение всех бронирований")
    void findAllByBookerIdOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdOrderByStartDateDesc(otherUser.getId(), Pageable.ofSize(10)).getContent();
        assertEquals(3, bookings.size());
        assertEquals(nextBooking, bookings.get(0));
        assertEquals(booking, bookings.get(1));
        assertEquals(lastBooking, bookings.get(2));
    }

    @Test
    @DisplayName("Прошедших бронирований")
    void findAllByBookerIdAndEndDateBefore() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndEndDateBefore(otherUser.getId(), Pageable.ofSize(10)).getContent();

        assertEquals(1, bookings.size());
        assertEquals(lastBooking, bookings.get(0));
    }

    @Test
    @DisplayName("Будущие бронирования")
    void findAllByBookerIdAndStartDateAfter() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStartDateAfter(otherUser.getId(), Pageable.ofSize(10)).getContent();

        assertEquals(1, bookings.size());
        assertEquals(nextBooking, bookings.get(0));
    }

    @Test
    @DisplayName("Текущие бронирований")
    void findAllByBookerIdAndDateBeforeAndDateAfter() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndDateBeforeAndDateAfter(otherUser.getId(), Pageable.ofSize(10)).getContent();

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    @DisplayName("Получение всех бронирований с определенным статусом")
    void findAllByBookerIdAndStatusOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDateDesc(otherUser.getId(), APPROVED,
                        Pageable.ofSize(10)).getContent();

        assertEquals(3, bookings.size());
        assertEquals(nextBooking, bookings.get(0));
        assertEquals(booking, bookings.get(1));
        assertEquals(lastBooking, bookings.get(2));
    }

    @Test
    @DisplayName("Получение всех бронирований хозяином вещи в убывании даты начала")
    void findAllByOwnerIdOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository
                .findAllByOwnerIdOrderByStartDateDesc(user.getId(), Pageable.ofSize(10)).getContent();
        assertEquals(3, bookings.size());
        assertEquals(nextBooking, bookings.get(0));
        assertEquals(booking, bookings.get(1));
        assertEquals(lastBooking, bookings.get(2));
    }

    @Test
    @DisplayName("Получение всех прошедших бронирований владельцем вещи")
    void findAllByOwnerIdAndEndDateBefore() {
        List<Booking> bookings = bookingRepository
                .findAllByOwnerIdAndEndDateBefore(user.getId(), Pageable.ofSize(10)).getContent();

        assertEquals(1, bookings.size());
        assertEquals(lastBooking, bookings.get(0));
    }

    @Test
    @DisplayName("Получение всех будущих бронирований владельцем вещи")
    void findAllByOwnerIdAndStartDateAfter() {
        List<Booking> bookings = bookingRepository
                .findAllByOwnerIdAndStartDateAfter(user.getId(), Pageable.ofSize(10)).getContent();
        assertEquals(1, bookings.size());
        assertEquals(nextBooking, bookings.get(0));
    }

    @Test
    @DisplayName("Получение всех текущих бронирований владельцем вещи")
    void findAllByOwnerIdAndDateBeforeAndDateAfter() {
        List<Booking> bookings = bookingRepository
                .findAllByOwnerIdAndDateBeforeAndDateAfter(user.getId(), Pageable.ofSize(10)).getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    @DisplayName("Получение всех бронирований владельцем вещи с определенным статусом")
    void findAllByOwnerIdAndStatusOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository
                .findAllByOwnerIdAndStatusOrderByStartDateDesc(user.getId(), APPROVED,
                        Pageable.ofSize(10)).getContent();
        assertEquals(3, bookings.size());
        assertEquals(nextBooking, bookings.get(0));
        assertEquals(booking, bookings.get(1));
        assertEquals(lastBooking, bookings.get(2));
    }

    @Test
    @DisplayName("Получение последнего бронирования вещи")
    void findFirstByItemIdAndEndDateBefore() {
        List<Booking> bookings = bookingRepository
                .findFirstByItemIdAndEndDateBefore(item.getId());

        assertEquals(2, bookings.size());
        assertEquals(booking, bookings.get(0));
        assertEquals(lastBooking, bookings.get(1));
    }

    @Test
    @DisplayName("Получение будущих бронирований со статусом APPROVED")
    void findFirstByItemIdAndStartDateAfter() {
        List<Booking> bookings = bookingRepository
                .findFirstByItemIdAndStartDateAfter(item.getId());

        assertEquals(1, bookings.size());
        assertEquals(nextBooking, bookings.get(0));
    }

    @Test
    @DisplayName("Получение бронирований вещи пользователем")
    void findAllByItemIdAndBookerId() {
        List<Booking> bookings = bookingRepository
                .findAllByItemIdAndBookerId(item.getId(), otherUser.getId());
        assertEquals(3, bookings.size());
        assertEquals(nextBooking, bookings.get(2));
        assertEquals(booking, bookings.get(0));
        assertEquals(lastBooking, bookings.get(1));

    }
}

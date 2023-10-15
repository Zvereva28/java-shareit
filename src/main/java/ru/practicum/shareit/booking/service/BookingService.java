package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(long userId, BookingDto bookingDto);

    BookingDto approvingBooking(long userId, long id, boolean approved);

    BookingDto getBooking(long userId, long id);

    List<BookingDto> getAllBookingByOwner(long userId, String state);

    List<BookingDto> getUserAllBooking(long userId, String state);
}

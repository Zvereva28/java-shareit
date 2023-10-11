package ru.practicum.shareit.booking.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;


@Mapper
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    Booking toBooking(BookingDto bookingDto);

    BookingReplyDto toBookingReplyDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    LastBookingDto lastBookingDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    NextBookingDto nextBookingDto(Booking booking);
}
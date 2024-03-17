package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.model.UserBookingDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingReplyDto extends BookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private UserBookingDto booker;

    private ItemBookingDto item;

    private String status;
}

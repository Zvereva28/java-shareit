package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;

}


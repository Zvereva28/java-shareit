package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDate;

@Data
public class BookingDto {

    private int id;

    private String name;

    private LocalDate start;

    private LocalDate end;

    private int itemId;

    private int bookerId;

    private Status status;

}

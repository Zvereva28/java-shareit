package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class Booking {

    private int id;

    @NotNull(message = "name may not be null")
    private String name;

    @NotNull(message = "start may not be null")
    private LocalDate start;

    @NotNull(message = "end may not be null")
    private LocalDate end;

    @NotNull(message = "end may not be null")
    private int item;

    @NotNull(message = "booker may not be null")
    private int booker;

    private Status status;

}

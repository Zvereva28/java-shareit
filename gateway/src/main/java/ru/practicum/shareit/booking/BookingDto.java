package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {

    private Long id;

    @NotNull
    @FutureOrPresent(message = "start аренды не должен быть в прошлом")
    private LocalDateTime start;

    @NotNull
    @Future(message = "end аренды не должен быть в прошлом")
    private LocalDateTime end;

    private long itemId;

}

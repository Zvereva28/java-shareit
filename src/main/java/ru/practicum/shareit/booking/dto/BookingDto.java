package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingDto {

    private Long id;

    @NotNull
    @FutureOrPresent(message = "Начало аренды не должно быть в прошлом")
    private LocalDateTime start;

    @NotNull
    @Future(message = "Завершение аренды не должно быть в прошлом")
    private LocalDateTime end;

    private long itemId;

}


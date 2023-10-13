package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ItemOwnerDto extends ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private LastBookingDto lastBooking;

    private NextBookingDto nextBooking;

    private List<CommentDto> comments = new ArrayList<>();

}
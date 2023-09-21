package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {

    private int id;

    private String name;

    private String description;

    private Boolean available;

    private int owner;

    private int requestId;
}

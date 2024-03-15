package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ItemDto {

    private Long id;

    @NotBlank(message = "name не может быть пустым")
    private String name;

    @NotBlank(message = "description не может быть пустым")
    private String description;

    @NotNull(message = "available не может быть пустым")
    private Boolean available;

    private Long requestId;
}


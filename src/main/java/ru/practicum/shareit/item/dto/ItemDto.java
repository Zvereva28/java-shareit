package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(message = "name не может быть пустым")
    private String name;

    @NotNull(message = "available не может быть пустым")
    private Boolean available;

    @NotBlank(message = "description не может быть пустым")
    private String description;

    private Long requestId;

}

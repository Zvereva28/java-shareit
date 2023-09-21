package ru.practicum.shareit.request.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ItemRequest {

    private int id;

    @NotNull(message = "description may not be null")
    private String description;

    @NotNull(message = "requestor may not be null")
    private String requestor;

    @NotNull
    private LocalDate created;

}

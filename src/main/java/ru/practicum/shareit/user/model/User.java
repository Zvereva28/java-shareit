package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class User {

    private int id;

    @NotNull(message = "email may not be null")
    @Email(message = "email не корректный")
    private String email;

    private String name;

}

package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.*;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class UserDto {

    @Positive
    private Long id;

    @Email
    @NotBlank
    private String email;

    @Size(max = 255)
    @NotBlank
    private String name;

}

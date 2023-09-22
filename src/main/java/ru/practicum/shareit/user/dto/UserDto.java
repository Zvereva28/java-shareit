package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int id;

    @NotNull(message = "email may not be null")
    @Email(message = "email не корректный")
    private String email;

    private String name;

    private Boolean active;

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                true
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(), userDto.email, userDto.name);
    }
}

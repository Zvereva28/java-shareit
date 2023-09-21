package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int id;

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

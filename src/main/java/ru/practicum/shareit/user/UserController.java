package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Validated UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable int id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }
}

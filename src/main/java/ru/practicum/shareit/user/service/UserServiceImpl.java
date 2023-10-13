package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository repository;


    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        User user = repository.save(UserMapper.INSTANCE.toUser(userDto));
        log.debug("Создан новый пользователь: '{}'", user);
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = ifUserExistReturnUser(userId);
        isEmailExist(userId, userDto.getEmail());
        user.setName(userDto.getName() != null ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail());
        repository.saveAndFlush(user);
        log.debug("Пользователь '{}' - обновлен", user);
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Override
    public UserDto getUser(long userId) {
        User user = ifUserExistReturnUser(userId);
        log.debug("Получен пользователь '{}'", user);
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        repository.deleteById(userId);
        log.debug("Пользователь с id '{}' - удален", userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.INSTANCE.toUserDtoList(repository.findAll());
    }

    private User ifUserExistReturnUser(long userId) {
        return repository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователя с id %d нет в базе", userId)));
    }

    private void isEmailExist(long userId, String email) {
        Optional<User> user = repository.findByEmail(email);
        if (user.isEmpty()) {
            return;
        } else if (user.get().getId() != userId) {
            throw new EmailException(
                    String.format("Email %s уже существует", email)
            );
        }
    }

}

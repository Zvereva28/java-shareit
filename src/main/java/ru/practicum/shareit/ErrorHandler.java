package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.exeption.ItemException;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.user.exception.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseBody handleUserException(final UserException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseBody handleUserException(final UserNotFoundException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseBody handleEmailException(final EmailException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseBody handleItemException(final ItemNotFoundException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseBody handleArgumentException(final MethodArgumentNotValidException e) {
        return new ResponseBody(
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseBody handleThrowable(final Throwable e) {
        return new ResponseBody(
                "Произошла непредвиденная ошибка."
        );
    }

}
package ru.practicum.shareit.item.exeption;

public class ItemException extends RuntimeException {

    public ItemException() {
        super();
    }

    public ItemException(final String message) {
        super(message);
    }

    public ItemException(String message, Throwable cause) {
        super(message, cause);
    }


}

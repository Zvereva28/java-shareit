package ru.practicum.shareit.booking.exception;

public class BookingException extends RuntimeException {

    public BookingException() {
        super();
    }

    public BookingException(final String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }

}
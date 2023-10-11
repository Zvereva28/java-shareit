package ru.practicum.shareit.booking.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BookingStatus {
    WAITING,
    REJECTED,
    APPROVED,
    CURRENT,
    FUTURE,
    PAST,
    ALL
}

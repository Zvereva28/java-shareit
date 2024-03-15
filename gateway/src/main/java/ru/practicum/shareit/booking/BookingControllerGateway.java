package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingControllerGateway {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) Integer size) {
        return bookingClient.getAllBookingByOwner(userId, state, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getUserAllBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) Integer size) {
        return bookingClient.getUserAllBooking(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvingBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam("approved") boolean approved) {
        return bookingClient.approvingBooking(userId, bookingId, approved);
    }

}

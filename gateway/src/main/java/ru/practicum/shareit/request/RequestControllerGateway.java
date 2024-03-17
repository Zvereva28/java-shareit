package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class RequestControllerGateway {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestClient.createItemRequest(userId, itemRequestDto);

    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId) {
        return requestClient.getItemRequest(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItemsRequest(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.getAllUserItemsRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) Integer size) {
        return requestClient.getAllItems(userId, from, size);
    }


}

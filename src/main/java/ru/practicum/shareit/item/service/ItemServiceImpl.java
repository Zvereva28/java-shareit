package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.exeption.ItemBookerException;
import ru.practicum.shareit.item.exeption.ItemException;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemMapper itemMapper;


    @Transactional
    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User user = ifUserExistReturnUser(userId);
        Item item = itemRepository.save(
                itemMapper.toItem(itemDto));
        item.setUser(user);
        log.debug("Создана новая вещь - '{}'", item);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId()).orElseThrow();
            item.setRequest(request);
        }

        return itemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        User user = ifUserExistReturnUser(userId);
        Item item = ifItemExistReturnItem(itemId);
        itemOwnerCheck(user.getId(), item.getUser().getId());
        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable());
        log.debug("Вещь '{}' - обновлена", item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long itemId, long userId) {
        Item item = ifItemExistReturnItem(itemId);
        List<CommentDto> comments = getCommentsByItemId(itemId);

        if (item.getUser().getId() != userId) {
            ItemOwnerDto itemOwnerDto = itemMapper.toItemOwnerDto(item);
            itemOwnerDto.setComments(comments);
            log.debug("Получена вещь '{}'", item);
            return itemOwnerDto;
        }
        List<Booking> lastBookings = bookingRepository.findFirstByItemIdAndEndDateBefore(itemId);
        Booking lastBooking = lastBookings.stream().findFirst().orElse(null);
        List<Booking> nextBookings = bookingRepository.findFirstByItemIdAndStartDateAfter(itemId);
        Booking nextBooking = nextBookings.stream().findFirst().orElse(null);

        ItemOwnerDto itemOwnerDto = itemMapper.toItemOwnerDto(item);
        itemOwnerDto.setLastBooking(BookingMapper.INSTANCE.lastBookingDto(lastBooking));
        itemOwnerDto.setNextBooking(BookingMapper.INSTANCE.nextBookingDto(nextBooking));
        itemOwnerDto.setComments(comments);

        log.debug("Владельцем получена вещь '{}'", itemOwnerDto);

        return itemOwnerDto;
    }

    @Override
    public List<ItemDto> getAllItems(long userId, int from, int size) {
        ifUserExistReturnUser(userId);
        Pageable pageable =
                PageRequest.of(from, size);
        final Page<Item> itemsPage = itemRepository.findAllByUserIdOrderByIdAsc(userId, pageable);
        List<Item> items = itemsPage.getContent();
        List<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : items) {
            ItemOwnerDto itemDto = itemMapper.toItemOwnerDto(item);
            List<Booking> lastBookings = bookingRepository.findFirstByItemIdAndEndDateBefore(item.getId());
            Booking lastBooking = lastBookings.stream().findFirst().orElse(null);
            List<Booking> nextBookings = bookingRepository.findFirstByItemIdAndStartDateAfter(item.getId());
            Booking nextBooking = nextBookings.stream().findFirst().orElse(null);
            List<CommentDto> comments = getCommentsByItemId(item.getId());

            itemDto.setLastBooking(BookingMapper.INSTANCE.lastBookingDto(lastBooking));
            itemDto.setNextBooking(BookingMapper.INSTANCE.nextBookingDto(nextBooking));
            itemDto.setComments(comments);
            itemsDto.add(itemDto);
        }
        log.debug("Получен список вещей пользователя с id '{}'", userId);
        return itemsDto;

    }

    @Override
    public List<ItemDto> searchItem(long userId, String searchText, int from, int size) {
        if (searchText.isEmpty()) {
            log.debug("Не было найдено ни одного предмета по запросу '{}'", searchText);
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from, size);
        final Page<Item> itemsPage = itemRepository.findByUserAndNameOrDescription(userId, searchText, pageable);
        List<ItemDto> items = itemsPage
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        return items;
    }

    @Override
    public Item ifItemExistReturnItem(long itemId) {
        return itemRepository.findById(itemId).stream().findFirst().orElseThrow(() -> new ItemNotFoundException(
                String.format("Вещи с id %d нет в базе", itemId)));
    }

    @Override
    @Transactional
    public CommentDto postComment(long userId, long itemId, CommentDto commentDto) {
        User user = ifUserExistReturnUser(userId);
        Item item = ifItemExistReturnItem(itemId);
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerId(itemId, userId)
                .stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new ItemBookerException(
                    String.format("Вещь с id %d не была арендована пользователем с id %d", itemId, userId));
        }
        Comment comment = commentRepository.save(CommentMapper.INSTANCE.toComment(commentDto));
        comment.setItem(item);
        comment.setAuthor(user);

        return CommentMapper.INSTANCE.toCommentDto(comment);
    }

    private void itemOwnerCheck(long userId, long itemId) {
        if (itemId != userId) {
            throw new ItemException(
                    String.format("Вещь с id %d не принадлежит пользователю с id = %d", itemId, userId));
        }
    }

    private List<CommentDto> getCommentsByItemId(long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

    private User ifUserExistReturnUser(long userId) {
        return userRepository.findById(userId).stream().findFirst().orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователя с id %d нет в базе", userId)));
    }
}
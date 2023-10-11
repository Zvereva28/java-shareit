package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exeption.*;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
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


    @Transactional
    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User user = ifUserExistReturnUser(userId);
        Item item = itemRepository.save(ItemMapper.INSTANCE.toItem(itemDto));
        item.setUser(user);
        log.info("Создана новая вещь - '{}'", item);

        return ItemMapper.INSTANCE.toItemDto(item);
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
        log.info("Вещь '{}' - обновлена", item);

        return ItemMapper.INSTANCE.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long itemId, long userId) {
        Item item = ifItemExistReturnItem(itemId);
        List<CommentDto> comments = getCommentsByItemId(itemId);

        if (item.getUser().getId() != userId) {
            ItemOwnerDto itemOwnerDto = ItemMapper.INSTANCE.toItemOwnerDto(item);
            itemOwnerDto.setComments(comments);
            log.info("Получена вещь '{}'", item);
            return itemOwnerDto;
        }
        List<Booking> lastBookings = bookingRepository.findFirstByItemIdAndEndDateBefore(itemId);
        Booking lastBooking = lastBookings.stream().findFirst().orElse(null);
        List<Booking> nextBookings = bookingRepository.findFirstByItemIdAndStartDateAfter(itemId);
        Booking nextBooking = nextBookings.stream().findFirst().orElse(null);

        ItemOwnerDto itemOwnerDto = ItemMapper.INSTANCE.toItemOwnerDto(item);
        itemOwnerDto.setLastBooking(BookingMapper.INSTANCE.lastBookingDto(lastBooking));
        itemOwnerDto.setNextBooking(BookingMapper.INSTANCE.nextBookingDto(nextBooking));
        itemOwnerDto.setComments(comments);

        log.info("Владельцем получена вещь '{}'", itemOwnerDto);

        return itemOwnerDto;
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        ifUserExistReturnUser(userId);

        List<Item> items = itemRepository.findAllByUserIdOrderByIdAsc(userId);
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            ItemOwnerDto itemDto = ItemMapper.INSTANCE.toItemOwnerDto(item);
            List<Booking> lastBookings = bookingRepository.findFirstByItemIdAndEndDateBefore(item.getId());
            Booking lastBooking = lastBookings.stream().findFirst().orElse(null);
            List<Booking> nextBookings = bookingRepository.findFirstByItemIdAndStartDateAfter(item.getId());
            Booking nextBooking = nextBookings.stream().findFirst().orElse(null);
            List<CommentDto> comments = getCommentsByItemId(item.getId());

            itemDto.setLastBooking(BookingMapper.INSTANCE.lastBookingDto(lastBooking));
            itemDto.setNextBooking(BookingMapper.INSTANCE.nextBookingDto(nextBooking));
            itemDto.setComments(comments);
            itemDtos.add(itemDto);
        }
        log.info("Получен список вещей пользователя с id '{}'", userId);
        return itemDtos;

    }

    @Override
    public List<ItemDto> searchItem(long userId, String searchText) {
        if (searchText.isEmpty()) {
            log.info("Не было найдено ни одного предмета по запросу '{}'", searchText);
            return Collections.emptyList();
        }
        List<ItemDto> items = itemRepository.findByUserAndNameOrDescription(userId, searchText)
                .stream()
                .map(ItemMapper.INSTANCE::toItemDto)
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
        if (bookings.size() == 0) {
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
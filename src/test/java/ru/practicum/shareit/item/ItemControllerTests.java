package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.ValidationException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTests {
    long userId = 1L;
    long itemId = 2L;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setName("Серп");
        itemDto.setDescription("И молот тоже");
        itemDto.setAvailable(true);
    }

    @SneakyThrows
    @Test
    @DisplayName("Создание вещи")
    public void createItem_whenItemIsValid_thenItemCreated() {
        when(itemService.addItem(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService).addItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    @DisplayName("Создание вещи пользователем без имени")
    void createItem_whenItemNameIsInvalid_thenItemNotCreated() {
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("серп");
        itemDto.setAvailable(true);
        when(itemService.addItem(userId, itemDto)).thenThrow(ValidationException.class);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(userId, itemDto);
    }

    @SneakyThrows
    @Test
    @DisplayName("Создание вещи без описания")
    void createItem_whenItemDescriptionIsInvalid_thenItemNotCreated() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Серп");
        itemDto.setAvailable(true);
        when(itemService.addItem(userId, itemDto)).thenThrow(ValidationException.class);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).addItem(userId, itemDto);
    }

    @SneakyThrows
    @Test
    @DisplayName("Создание вещи пользователем с ошибкой доступа")
    void createItem_whenItemAvailableIsInvalid_thenItemNotCreated() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Серп");
        itemDto.setDescription("Острый и красный");
        when(itemService.addItem(userId, itemDto)).thenThrow(ValidationException.class);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(userId, itemDto);
    }

    @SneakyThrows
    @Test
    @DisplayName("Обновление вещи владельцем")
    void updateItemByOwner() {
        ItemDto updatedItem = makeItemDto(itemId, "Дрель+", "Аккумуляторная дрель", true);
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItem.getName())))
                .andExpect(jsonPath("$.description", is(updatedItem.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItem.getAvailable())));

    }

    @SneakyThrows
    @Test
    @DisplayName("Получение вещи")
    void getItemItemReturned() {
        when(itemService.getItem(itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService).getItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение несуществующей вещи")
    public void getItemNotFoundException() {
        itemId = 1000L;
        when(itemService.getItem(itemId, userId)).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private ItemDto makeItemDto(Long id, String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);

        return dto;
    }
}

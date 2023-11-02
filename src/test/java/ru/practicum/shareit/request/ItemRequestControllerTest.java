package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ItemRequestDto requestDto;
    @MockBean
    private ItemRequestService requestService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestDto();
        requestDto.setDescription("Подробное описание");
    }

    @Test
    @SneakyThrows
    @DisplayName("Создание запроса")
    public void createItemRequestSuccess() {
        when(requestService.createItemRequest(anyLong(), any())).thenReturn(requestDto);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
        verify(requestService).createItemRequest(anyLong(), any());
    }

    @Test
    @SneakyThrows
    @DisplayName("Не корректное Создание запроса")
    public void createRequestNotSuccess() {
        ItemRequestDto requestDtoNull = new ItemRequestDto();
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(requestDtoNull))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).createItemRequest(1L, requestDtoNull);
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение списка запросов пользователя")
    void getAllRequest() {
        when(requestService.getAllItems(2L, 0, 3)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())));

        verify(requestService).getAllItems(2L, 0, 3);
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение списка запросов без параметров")
    void getAllRequestNoParam() {
        when(requestService.getAllItems(5L, 0, 10)).thenReturn(List.of(requestDto));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())));
        verify(requestService).getAllItems(5L, 0, 10);
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение запроса")
    void getItemRequest() {
        long userId = 1L;
        long requestId = 1L;
        when(requestService.getItemRequest(userId, requestId)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));

        verify(requestService).getItemRequest(userId, requestId);
    }


}

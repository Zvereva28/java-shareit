package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequestReplyWithResponseDto {

    private int id;

    private String description;

    private String requestor;

    private LocalDate created;


}
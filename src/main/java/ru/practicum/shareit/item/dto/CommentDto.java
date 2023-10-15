package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDto {

    private Long id;

    @NotBlank(message = "text не может быть пустым")
    private String text;

    private String authorName;

    private LocalDateTime created = LocalDateTime.now();
}
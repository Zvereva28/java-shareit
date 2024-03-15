package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private Long id;

    @NotBlank(message = "text не может быть пустым")
    private String text;

    private String authorName;

    private LocalDateTime created = LocalDateTime.now();

}
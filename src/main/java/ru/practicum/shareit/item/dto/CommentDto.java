package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    public CommentDto(String text) {
        this.text = text;
    }
    private Long id;

    @NotEmpty
    @NotNull
    private String text;

    private String authorName;

    private Long authorId;

    private Long itemId;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'")
    private LocalDateTime created;
}


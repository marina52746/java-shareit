package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public static CommentDto fromCommentToCommentDto(Comment comment) {
        if (comment == null) return null;
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor() != null ? comment.getAuthor().getName() : "",
                comment.getAuthor() != null ? comment.getAuthor().getId() : 0,
                comment.getItem() != null ? comment.getItem().getId() : 0,
                comment.getCreated()
        );
    }

    public static Comment fromCommentDtoToComment(CommentDto commentDto, Item item, User user) {
        if (commentDto == null) return null;
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                commentDto.getCreated()
        );
    }

    public static CommentDto commentDtoSetValues(CommentDto commentDto,
                                                 Long authorId, String authorName, Long itemId) {
        commentDto.setAuthorId(authorId);
        commentDto.setAuthorName(authorName);
        commentDto.setItemId(itemId);
        commentDto.setCreated(LocalDateTime.now());
        return commentDto;
    }
}

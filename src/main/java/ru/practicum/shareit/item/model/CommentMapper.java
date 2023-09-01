package ru.practicum.shareit.item.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.UserMapper;

@Component
public class CommentMapper {
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;

    @Autowired
    public CommentMapper(UserService userService, BookingRepository bookingRepository) {
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.itemMapper = new ItemMapper(userService, bookingRepository);
    }

    public static CommentDto fromCommentToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getAuthor().getId(),
                comment.getItem().getId(),
                comment.getCreated()
        );
    }

    public Comment fromCommentDtoToComment(CommentDto commentDto, ItemService itemService) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                itemMapper.fromItemDtoToItem(itemService.getItemById(commentDto.getItemId())),
                UserMapper.fromUserDtoToUser(userService.getUserById(commentDto.getAuthorId())),
                commentDto.getCreated()
        );
    }
}

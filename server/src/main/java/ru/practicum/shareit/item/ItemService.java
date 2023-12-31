package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookingsAndComments;

import java.rmi.ServerException;
import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto) throws NotFoundException;

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) throws NotFoundException;

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) throws ServerException, NotFoundException;

    ItemDto getItemById(Long id);

    ItemWithBookingsAndComments getItemWithCommentsById(Long userId, Long itemId, Item item);

    List<ItemWithBookingsAndComments> getUserItems(Long userId, Pageable pageable);

    List<ItemDto> findByText(String text, Pageable pageable);

    List<Item> getAll();

    List<Comment> getAllComments();
}

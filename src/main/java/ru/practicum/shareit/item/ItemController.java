package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookingsAndComments;

import javax.validation.Valid;
import java.rmi.ServerException;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsAndComments getItemWithCommentsById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @PathVariable Long itemId) {
        return itemService.getItemWithCommentsById(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingsAndComments> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getUserItems(userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) throws NotFoundException {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto)
            throws ServerException, NotFoundException {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam("text") String text) {
        return itemService.findByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody CommentDto comment) throws NotFoundException {
        return itemService.createComment(userId, itemId, comment);
    }

    @GetMapping("/all")
    public List<Item> allItems() {
        return itemService.getAll();
    }

    @GetMapping("/allComments")
    public List<Comment> allComments() {
        return itemService.getAllComments();
    }
}

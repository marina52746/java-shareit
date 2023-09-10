package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookingsAndComments;
import ru.practicum.shareit.pagination.FromSizeRequest;

import java.rmi.ServerException;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

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
        ShareItServer.sharerUserId = userId;
        return itemService.getItemWithCommentsById(ShareItServer.sharerUserId, itemId, null);
    }

    @GetMapping
    public List<ItemWithBookingsAndComments> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        ShareItServer.sharerUserId = userId;
        final PageRequest pageRequest = FromSizeRequest.of(from, size, Sort.by(ASC, "id"));
        return itemService.getUserItems(ShareItServer.sharerUserId, pageRequest);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto) throws NotFoundException {
        ShareItServer.sharerUserId = userId;
        return itemService.createItem(ShareItServer.sharerUserId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto)
            throws ServerException, NotFoundException {
        ShareItServer.sharerUserId = userId;
        return itemService.updateItem(ShareItServer.sharerUserId, itemId, itemDto);
    }

    @GetMapping("/search") //items/search?text={text}
    public List<ItemDto> findByText(@RequestParam("text") String text,
                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @RequestParam(required = false, defaultValue = "10") Integer size) {
        final PageRequest pageRequest = FromSizeRequest.of(from, size, Sort.by(ASC, "id"));
        return itemService.findByText(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody CommentDto comment) throws NotFoundException {
        ShareItServer.sharerUserId = userId;
        return itemService.createComment(ShareItServer.sharerUserId, itemId, comment);
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

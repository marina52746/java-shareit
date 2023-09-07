package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookingsAndComments;
import ru.practicum.shareit.pagination.FromSizeRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
        ShareItApp.sharerUserId = userId;
        return itemService.getItemWithCommentsById(ShareItApp.sharerUserId, itemId, null);
    }

    @GetMapping
    public List<ItemWithBookingsAndComments> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        ShareItApp.sharerUserId = userId;
        final PageRequest pageRequest = FromSizeRequest.of(from, size, Sort.by(ASC, "id"));
        return itemService.getUserItems(ShareItApp.sharerUserId, pageRequest);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) throws NotFoundException {
        ShareItApp.sharerUserId = userId;
        return itemService.createItem(ShareItApp.sharerUserId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto)
            throws ServerException, NotFoundException {
        ShareItApp.sharerUserId = userId;
        return itemService.updateItem(ShareItApp.sharerUserId, itemId, itemDto);
    }

    @GetMapping("/search") //items/search?text={text}
    public List<ItemDto> findByText(@RequestParam("text") String text,
                                    @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        final PageRequest pageRequest = FromSizeRequest.of(from, size, Sort.by(ASC, "id"));
        return itemService.findByText(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody CommentDto comment) throws NotFoundException {
        ShareItApp.sharerUserId = userId;
        return itemService.createComment(ShareItApp.sharerUserId, itemId, comment);
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

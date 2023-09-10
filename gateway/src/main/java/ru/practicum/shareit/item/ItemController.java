package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.rmi.ServerException;

@RestController
@RequiredArgsConstructor
@Slf4j
//@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemWithCommentsById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @PathVariable Long itemId) {
        return itemClient.getItemWithCommentsById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return itemClient.getUserItems(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) throws NotFoundException {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto)
            throws ServerException, NotFoundException {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search") //items/search?text={text}
    public ResponseEntity<Object> findByText(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "text") String text,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return itemClient.findItemsByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody CommentDto comment) throws NotFoundException {
        return itemClient.createComment(userId, itemId, comment);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> allItems() {
        return itemClient.getAll();
    }

    @GetMapping("/allComments")
    public ResponseEntity<Object> allComments() {
        return itemClient.getAllComments();
    }
}

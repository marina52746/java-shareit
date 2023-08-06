package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

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
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
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

}

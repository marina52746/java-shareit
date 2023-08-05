package ru.practicum.shareit.item.model;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.rmi.ServerException;
import java.util.List;

public interface ItemService {

    Item createItem(Long userId, Item item) throws NotFoundException;

    Item updateItem(Long userId, Long itemId, ItemDto item) throws ServerException, NotFoundException;

    Item getItemById(Long id);

    List<Item> getUserItems(Long userId);

    void deleteItem(Long id);

    List<Item> getAllItems();

    List<Item> findByText(String text);
}

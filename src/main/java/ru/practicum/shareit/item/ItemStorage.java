package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.rmi.ServerException;
import java.util.List;

public interface ItemStorage {
    Item createItem(Long userId, Item item) throws NotFoundException;

    Item updateItem(Long userId, Long itemId, Item item) throws ServerException, NotFoundException;

    Item getItemById(Long id);

    void deleteItem(Long id);

    List<Item> getAllItems();

    List<Item> findByText(String text);

    List<Item> getUserItems(Long userId);
}

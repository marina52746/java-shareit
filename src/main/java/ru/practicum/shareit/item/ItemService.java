package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.rmi.ServerException;
import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto) throws NotFoundException;

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) throws ServerException, NotFoundException;

    ItemDto getItemById(Long id);

    List<ItemDto> getUserItems(Long userId);

    void deleteItem(Long id);

    List<ItemDto> getAllItems();

    List<ItemDto> findByText(String text);
}

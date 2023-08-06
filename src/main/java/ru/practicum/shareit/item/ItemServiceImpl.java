package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;

import java.rmi.ServerException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage userStorage) {
        this.itemStorage = userStorage;
    }


    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) throws NotFoundException {
        return ItemMapper.fromItemToItemDto(itemStorage.createItem(userId, ItemMapper.fromItemDtoToItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) throws ServerException, NotFoundException {
        patchItemDtoValidate(itemDto);
        return ItemMapper.fromItemToItemDto(itemStorage.updateItem(userId, itemId,
                ItemMapper.fromItemDtoToItem(itemDto)));
    }

    private void patchItemDtoValidate(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank())
            throw new ValidationException("Item name can't be empty!");
        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank())
            throw new ValidationException("Item description can't be empty!");
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.fromItemToItemDto(itemStorage.getItemById(id));
    }

    @Override
    public void deleteItem(Long id) {
        itemStorage.deleteItem(id);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemStorage.getAllItems().stream().map(x -> ItemMapper.fromItemToItemDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByText(String text) {
        return itemStorage.findByText(text).stream().map(x -> ItemMapper.fromItemToItemDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemStorage.getUserItems(userId).stream().map(x -> ItemMapper.fromItemToItemDto(x))
                .collect(Collectors.toList());
    }

}

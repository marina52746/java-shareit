package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemStorageInMemory implements ItemStorage {
    private final UserService userService;
    private static long itemsCount = 0;
    public Map<Long, Item> allItems = new HashMap<>();
    public Map<Long, List<Long>> usersItemsIds = new HashMap<>();

    public ItemStorageInMemory(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Item createItem(Long userId, Item item) throws NotFoundException {
        userService.getUserById(userId);
        item.setId(++itemsCount);
        allItems.put(item.getId(), item);
        if (usersItemsIds.get(userId) == null)
            usersItemsIds.put(userId, new ArrayList<>());
        usersItemsIds.get(userId).add(item.getId());
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) throws ServerException, NotFoundException {
        if (userId == null)
            throw new ServerException("X-Sharer-User_Id is absent");
        userService.getUserById(userId);
        if (!(usersItemsIds.get(userId) != null && usersItemsIds.get(userId).contains(itemId)))
            throw new NotFoundException("This user isn't thing's owner");
        Item newItem = allItems.get(itemId);
        if (item.getName() != null) newItem.setName(item.getName());
        if (item.getDescription() != null) newItem.setDescription(item.getDescription());
        if (item.getAvailable() != null) newItem.setAvailable(item.getAvailable());
        allItems.put(itemId, newItem);
        usersItemsIds.get(userId).add(itemId);
        return newItem;
    }

    @Override
    public Item getItemById(Long id) {
        return allItems.get(id);
    }

    @Override
    public void deleteItem(Long id) {
        allItems.remove(id);
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<Item>(allItems.values());
    }

    @Override
    public List<Item> findByText(String text) {
        if (text.isEmpty()) return new ArrayList<>();
        return allItems.values().stream()
                .filter(x -> (x.getDescription().toLowerCase().contains(text.toLowerCase())
                        || x.getName().toLowerCase().contains(text.toLowerCase())) && x.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return allItems.values().stream()
                .filter(x -> usersItemsIds.get(userId).contains(x.getId()))
                .collect(Collectors.toList());
    }
}

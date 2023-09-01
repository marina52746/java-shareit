package ru.practicum.shareit.item.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.UserMapper;

@Component
public class ItemMapper {
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemMapper(UserService userService, BookingRepository bookingRepository) {
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }
    public ItemDto fromItemToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId()
        );
    }

    public Item fromItemDtoToItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                UserMapper.fromUserDtoToUser(userService.getUserById(itemDto.getOwnerId()))
        );
    }
}

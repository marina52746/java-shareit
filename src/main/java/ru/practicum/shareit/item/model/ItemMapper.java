package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.OwnerBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemMapper {

    public ItemDto fromItemToItemDto(Item item) {
        if (item == null) return null;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : 0
        );
    }

    public Item fromItemDtoToItem(ItemDto itemDto, User user) {
        if (itemDto == null) return null;
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user
        );
    }

    public ItemWithBookingsAndComments fromItemToItemWithBookingsAndComments(
            Item item, OwnerBookingDto lastBooking, OwnerBookingDto nextBooking, List<CommentDto> comments) {
        if (item == null) return null;
        return new ItemWithBookingsAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }
}

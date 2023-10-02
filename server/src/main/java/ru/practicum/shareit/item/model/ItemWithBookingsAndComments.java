package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.OwnerBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingsAndComments {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private OwnerBookingDto lastBooking;
    private OwnerBookingDto nextBooking;
    private List<CommentDto> comments;
}

package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OwnerBookingDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {

    public BookingDto fromBookingToBookingDto(Booking booking) {
        if (booking == null) return null;
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ?  new ItemShortDto(booking.getItem().getId(), booking.getItem().getName())
                    : null,
                booking.getBooker() != null ? new UserShortDto(booking.getBooker().getId()) : null,
                booking.getStatus()
        );
    }

    public OwnerBookingDto fromBookingToOwnerBookingDto(Booking booking) {
        if (booking == null) return null;
        return new OwnerBookingDto(
                booking.getId(),
                booking.getBooker() != null ? booking.getBooker().getId() : 0);
    }

    public Booking fromBookingDtoToBooking(BookingDto bookingDto, Item item, User user) {
        if (bookingDto == null) return null;
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus()
        );
    }

    public Booking fromInputBookingDtoToBooking(InputBookingDto inputBookingDto, Item item, User user) {
        if (inputBookingDto == null) return null;
        return new Booking(
                inputBookingDto.getStart(),
                inputBookingDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }
}


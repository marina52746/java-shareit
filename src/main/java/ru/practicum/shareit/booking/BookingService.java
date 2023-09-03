package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto getBookingOrThrow(long userId, long bookingId);

    BookingDto create(InputBookingDto inputBookingDto);

    BookingDto update(Long ownerId, Long bookingId, Boolean approved);

    List<BookingDto> getBookerBookingsByState(Long bookerId, String state);

    List<BookingDto> getOwnerBookingsByState(Long ownerId, String state);

    List<BookingDto> getAll();

}

package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    Booking getBookingOrThrow(long userId, long bookingId);

    Booking create(BookingDto bookingDto);

    Booking update(Long ownerId, Long bookingId, Boolean approved);

    List<Booking> getBookerBookingsByState(Long bookerId, String state);

    List<Booking> getOwnerBookingsByState(Long ownerId, String state);

    List<Booking> getAll();

}

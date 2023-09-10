package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto getBookingOrThrow(long userId, long bookingId);

    BookingDto create(InputBookingDto inputBookingDto);

    BookingDto update(Long ownerId, Long bookingId, Boolean approved);

    List<BookingDto> getBookerBookingsByState(Long bookerId, String state, Pageable pageable);

    List<BookingDto> getOwnerBookingsByState(Long ownerId, String state, Pageable pageable);

    List<BookingDto> getAll();

}

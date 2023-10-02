package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.pagination.FromSizeRequest;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody InputBookingDto inputBookingDto) {
        ShareItServer.sharerUserId = userId;
        inputBookingDto.setBookerId(ShareItServer.sharerUserId);
        return bookingService.create(inputBookingDto);
    }

    @PatchMapping("/{bookingId}") // ?approved={approved}
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @PathVariable Long bookingId, @RequestParam Boolean approved) {
        ShareItServer.sharerUserId = ownerId;
        return bookingService.update(ShareItServer.sharerUserId, bookingId, approved);
    }

    //bookings?state={state}
    @GetMapping
    public List<BookingDto> bookerBookings(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        ShareItServer.sharerUserId = bookerId;
        final PageRequest pageRequest = FromSizeRequest.of(from, size, Sort.by(DESC, "end"));
        return bookingService.getBookerBookingsByState(ShareItServer.sharerUserId, state, pageRequest);

    }

    // /bookings/owner?state={state}
    @GetMapping("/owner")
    public List<BookingDto> ownerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        ShareItServer.sharerUserId = ownerId;
        final PageRequest pageRequest = FromSizeRequest.of(from, size, Sort.by(DESC, "end"));
        return bookingService.getOwnerBookingsByState(ShareItServer.sharerUserId, state, pageRequest);
    }

    @GetMapping("/all")
    public List<BookingDto> allBookings() {
        return bookingService.getAll();
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        ShareItServer.sharerUserId = userId;
        return bookingService.getBookingOrThrow(ShareItServer.sharerUserId, bookingId);
    }
}

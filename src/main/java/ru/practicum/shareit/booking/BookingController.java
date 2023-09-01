package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid BookingDto bookingDto) {
        bookingDto.setBookerId(userId);
        bookingDto.setStatus(BookingStatus.WAITING);
        return bookingService.create(bookingDto);
    }

    @PatchMapping("/{bookingId}") // ?approved={approved}
    public Booking update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.update(ownerId, bookingId, approved);
    }

    //bookings?state={state}
    @GetMapping
    public List<Booking> bookerBookings (@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                     @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookerBookingsByState(bookerId, state);

    }

    // /bookings/owner?state={state}
    @GetMapping("/owner")
    public List<Booking> ownerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookingsByState(ownerId, state);
    }

    @GetMapping("/all")
    public List<Booking> allBookings() {
        return bookingService.getAll();
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingOrThrow(userId, bookingId);
        return booking;
    }
}

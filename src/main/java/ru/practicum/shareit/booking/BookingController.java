package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody @Valid InputBookingDto inputBookingDto) {
        ShareItApp.sharerUserId = userId;
        inputBookingDto.setBookerId(ShareItApp.sharerUserId);
        return bookingService.create(inputBookingDto);
    }

    @PatchMapping("/{bookingId}") // ?approved={approved}
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @PathVariable Long bookingId, @RequestParam Boolean approved) {
        ShareItApp.sharerUserId = ownerId;
        return bookingService.update(ShareItApp.sharerUserId, bookingId, approved);
    }

    //bookings?state={state}
    @GetMapping
    public List<BookingDto> bookerBookings (@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                     @RequestParam(required = false, defaultValue = "ALL") String state) {
        ShareItApp.sharerUserId = bookerId;
        return bookingService.getBookerBookingsByState(ShareItApp.sharerUserId, state);

    }

    // /bookings/owner?state={state}
    @GetMapping("/owner")
    public List<BookingDto> ownerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @RequestParam(required = false, defaultValue = "ALL") String state) {
        ShareItApp.sharerUserId = ownerId;
        return bookingService.getOwnerBookingsByState(ShareItApp.sharerUserId, state);
    }

    @GetMapping("/all")
    public List<BookingDto> allBookings() {
        return bookingService.getAll();
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        ShareItApp.sharerUserId = userId;
        return bookingService.getBookingOrThrow(ShareItApp.sharerUserId, bookingId);
    }
}

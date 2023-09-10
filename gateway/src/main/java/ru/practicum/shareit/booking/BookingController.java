package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> bookerBookings(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", required = false, defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
		State state = State.fromStringToState(stateParam);
		log.info("Get booker booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookerBookingsByState(userId, state, from, size);
	}

	// /bookings/owner?state={state}
	@GetMapping("/owner")
	public ResponseEntity<Object> ownerBookings(
			@RequestHeader("X-Sharer-User-Id") Long ownerId,
			@RequestParam(name = "state", required = false, defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
		State state = State.fromStringToState(stateParam);
		log.info("Get owner booking with state {}, userId={}, from={}, size={}", stateParam, ownerId, from, size);
		return bookingClient.getOwnerBookingsByState(ownerId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
										 @RequestBody @Valid InputBookingDto inputBookingDto) {
		log.info("Creating booking {}, userId={}", inputBookingDto, userId);
		return bookingClient.createBooking(userId, inputBookingDto);
	}

	@PatchMapping("/{bookingId}") // ?approved={approved}
	public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
							 @PathVariable Long bookingId, @RequestParam(name = "approved") Boolean approved) {
		return bookingClient.updateBooking(ownerId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> allBookings() {
		return bookingClient.getAll();
	}

}

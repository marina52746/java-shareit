package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(UserService userService, ItemService itemService, BookingRepository bookingRepository) {
        this.itemService = itemService;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = new BookingMapper(itemService, userService, bookingRepository);
    }

    public Booking getBookingOrThrow(long userId, long bookingId) {
        UserDto user = userService.getUserById(userId);
        try {
            Booking booking = bookingRepository
                    .findById(bookingId)
                    .orElseThrow(NoSuchElementException::new);
            if (booking.getBooker().getId() != userId
                    && booking.getItem().getOwner().getId() != userId)
                throw new IllegalArgumentException("Don't have access!");
            return booking;
        } catch (Exception NoSuchElementException) {
            throw new NotFoundException("Booking with id = " + bookingId + " not found");
        }
    }

    @Override
    public Booking create(BookingDto bookingDto) {
        LocalDateTime dateTime = LocalDateTime.now();
        userService.getUserById(bookingDto.getBookerId());
        var item = itemService.getItemById(bookingDto.getItemId());
        if (!(item.getAvailable()))
            throw new ValidationException("Item " + item.getId() + " is not available");
        if (bookingDto.getBookerId() == itemService.getItemById(bookingDto.getItemId()).getOwnerId())
            throw new NotFoundException("User can't book his own thing");
        if (bookingDto.getStart().isBefore(dateTime) || bookingDto.getEnd().isBefore(dateTime))
            throw new ValidationException("Start and End date must not be before now");
        if(bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd()))
            throw new ValidationException("Start date must be before end date");
        return bookingRepository.save(bookingMapper.fromBookingDtoToBooking(bookingDto));
    }

    @Override
    public Booking update(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = getBookingOrThrow(ownerId, bookingId);
        if (booking.getBooker().getId() == ownerId)
            throw new NotFoundException("Booker can't approve booking");
        if (booking.getStatus() == BookingStatus.APPROVED)
            throw new ValidationException("Booking is already approved");
        if (approved)
            booking.setStatus(BookingStatus.APPROVED);
        else
            booking.setStatus(BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookerBookingsByState(Long bookerId, String state) /*throws UnknownStateException*/ {
        userService.getUserById(bookerId);
        switch (state) {
            case "REJECTED":
                return bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED,
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.WAITING,
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().collect(Collectors.toList());
            case "ALL":
                return bookingRepository.findByBooker_Id(bookerId,
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByBooker_IdAndEndIsBefore(bookerId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByBooker_IdAndStartIsAfter(bookerId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(bookerId, LocalDateTime.now(),
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "end")).stream()
                        .collect(Collectors.toList());
            default:
                throw new UnknownStateException("Unknown state: " + state, "Unknown state: " + state);
        }
    }

    @Override
    public List<Booking> getOwnerBookingsByState(Long ownerId, String state) {
        userService.getUserById(ownerId);
        switch (state) {
            case "REJECTED":
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().filter(x -> x.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().filter(x -> x.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
            case "ALL":
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().filter(x -> x.getStart().isAfter(LocalDateTime.now())
                                && x.getStatus() == BookingStatus.APPROVED)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().filter(x -> x.getStart().isBefore(LocalDateTime.now())
                                && x.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            default:
                throw new UnknownStateException("Unknown state: " + state, "Unknown state: " + state);
        }
    }

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }
}

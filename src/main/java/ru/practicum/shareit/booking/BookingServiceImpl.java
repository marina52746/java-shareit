package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final UserMapper userMapper = new UserMapper();
    private final ItemMapper itemMapper = new ItemMapper();
    private final BookingMapper bookingMapper = new BookingMapper();

    @Autowired
    public BookingServiceImpl(UserService userService, ItemService itemService, BookingRepository bookingRepository) {
        this.itemService = itemService;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }

    public BookingDto getBookingOrThrow(long userId, long bookingId) {
        userService.getUserById(userId);
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id = \" + bookingId + \" not found"));
        if (booking.getBooker().getId() != userId
                && booking.getItem().getOwner().getId() != userId)
            throw new NotFoundException("Don't have access!");
        return bookingMapper.fromBookingToBookingDto(booking);
    }

    @Override
    public BookingDto create(InputBookingDto inputBookingDto) {
        LocalDateTime dateTime = LocalDateTime.now();
        User user = userMapper.fromUserDtoToUser(userService.getUserById(inputBookingDto.getBookerId()));
        Item item = itemMapper.fromItemDtoToItem(itemService.getItemById(inputBookingDto.getItemId()), user);
        if (!(item.getAvailable()))
            throw new ValidationException("Item " + item.getId() + " is not available");
        if (inputBookingDto.getBookerId() == itemService.getItemById(inputBookingDto.getItemId()).getOwnerId())
            throw new NotFoundException("User can't book his own thing");
        if (inputBookingDto.getStart().isBefore(dateTime) || inputBookingDto.getEnd().isBefore(dateTime))
            throw new ValidationException("Start and End date must not be before now");
        if(inputBookingDto.getStart().isAfter(inputBookingDto.getEnd()) || inputBookingDto.getStart().equals(inputBookingDto.getEnd()))
            throw new ValidationException("Start date must be before end date");
        return bookingMapper.fromBookingToBookingDto(bookingRepository
                .save(bookingMapper.fromInputBookingDtoToBooking(inputBookingDto, item, user)));
    }

    @Override
    public BookingDto update(Long ownerId, Long bookingId, Boolean approved) {
        BookingDto bookingDto = getBookingOrThrow(ownerId, bookingId);
        if (bookingDto.getBooker().getId() == ownerId)
            throw new NotFoundException("Booker can't approve booking");
        if (bookingDto.getStatus() == BookingStatus.APPROVED)
            throw new ValidationException("Booking is already approved");
        User user = userMapper.fromUserDtoToUser(userService.getUserById(bookingDto.getBooker().getId()));
        Item item = itemMapper.fromItemDtoToItem(itemService.getItemById(bookingDto.getItem().getId()), user);
        if (approved)
            bookingDto.setStatus(BookingStatus.APPROVED);
        else
            bookingDto.setStatus(BookingStatus.REJECTED);
        return bookingMapper.fromBookingToBookingDto(bookingRepository
                .save(bookingMapper.fromBookingDtoToBooking(bookingDto, item, user)));
    }

    @Override
    public List<BookingDto> getBookerBookingsByState(Long bookerId, String stateStr) {
        LocalDateTime dateTime = LocalDateTime.now();
        State state = State.fromStringToState(stateStr);
        userService.getUserById(bookerId);
        switch (state) {
            case REJECTED:
                return bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED,
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x)).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.WAITING,
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x)).collect(Collectors.toList());
            case ALL:
                return bookingRepository.findByBooker_Id(bookerId,
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x)).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBooker_IdAndEndIsBefore(bookerId, dateTime,
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x)).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBooker_IdAndStartIsAfter(bookerId, dateTime,
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x)).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(bookerId, LocalDateTime.now(),
                                dateTime, Sort.by(Sort.Direction.DESC, "end")).stream()
                        .map(x -> bookingMapper.fromBookingToBookingDto(x)).collect(Collectors.toList());
            default:
                throw new UnknownStateException("Unknown state: " + state, "Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDto> getOwnerBookingsByState(Long ownerId, String stateStr) {
        LocalDateTime dateTime = LocalDateTime.now();
        State state = State.fromStringToState(stateStr);
        userService.getUserById(ownerId);
        switch (state) {
            case REJECTED:
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x))
                        .filter(x -> x.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x))
                        .filter(x -> x.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
            case ALL:
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x)).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x))
                        .filter(x -> x.getEnd().isBefore(dateTime))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x))
                        .filter(x -> x.getStart().isAfter(dateTime)
                                && x.getStatus() == BookingStatus.APPROVED || x.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByOwner_IdOrderByEndDesc(ownerId)
                        .stream().map(x -> bookingMapper.fromBookingToBookingDto(x))
                        .filter(x -> x.getStart().isBefore(dateTime)
                                && x.getEnd().isAfter(dateTime))
                        .collect(Collectors.toList());
            default:
                throw new UnknownStateException("Unknown state: " + state, "Unknown state: " + state);
        }
    }

    public List<BookingDto> getAll() {
        return bookingRepository.findAll().stream()
                .map(x -> bookingMapper.fromBookingToBookingDto(x)).collect(Collectors.toList());
    }
}

package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OwnerBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class BookingMapper {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;

    public static Comparator<Booking> bookingComparator = new Comparator<Booking>() {
        @Override
        public int compare(Booking o1, Booking o2) {
            return o1.getStart().compareTo(o2.getStart());
        }
    };

    @Autowired
    public BookingMapper(ItemService itemService, UserService userService, BookingRepository bookingRepository) {
        this.itemService = itemService;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.itemMapper = new ItemMapper(userService, bookingRepository);
    }

    public BookingDto fromBookingToBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static OwnerBookingDto fromBookingToOwnerBookingDto(Optional<Booking> booking) {
        Booking optBooking = booking.orElse(null);
        if (optBooking == null)
            return null;
        else {
            return new OwnerBookingDto(
                    optBooking.getId(),
                    optBooking.getBooker().getId());
        }
    }

    public Booking fromBookingDtoToBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                itemMapper.fromItemDtoToItem(itemService.getItemById(bookingDto.getItemId())),
                UserMapper.fromUserDtoToUser(userService.getUserById(bookingDto.getBookerId())),
                bookingDto.getStatus()
        );
    }

    public Booking fromOwnerBookingDtoToBooking(OwnerBookingDto bookingDto) {
        try {
            return bookingRepository.findById(bookingDto.getId()).orElseThrow(NoSuchElementException::new);
        } catch (Exception NoSuchElementException) {
            throw new NotFoundException("Booking with id " + bookingDto.getId() + " doesn't exist");
        }
    }
}

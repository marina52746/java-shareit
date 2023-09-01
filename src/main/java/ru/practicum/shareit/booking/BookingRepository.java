package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT booking FROM Booking AS booking WHERE booking.item.id IN ?1 AND booking.status = 'APPROVED'")
    List<Booking> findApprovedBookingsFor(Collection<Item> items, Sort sort);

    @Query("SELECT booking FROM Booking AS booking WHERE booking.item IN " +
            "(SELECT item FROM Item AS item WHERE item.owner.id = ?1)")
    List<Booking> findByOwner_Id(Long ownerId, Sort sort);

    @Query("SELECT booking FROM Booking AS booking WHERE booking.item IN " +
            "(SELECT item FROM Item AS item WHERE item.owner.id = ?1)" +
            "ORDER BY booking.end DESC")
    List<Booking> findByOwner_IdOrderByEndDesc(Long ownerId);

    List<Booking> findByBooker_IdAndItem_IdAndStatusAndEndIsBefore(Long bookerId, Long itemId,
                                                                   BookingStatus status, LocalDateTime now);

    List<Booking> findByBooker_IdAndItem_IdAndStatusAndStartIsBefore(Long bookerId, Long itemId,
                                                                     BookingStatus status, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndItem_IdAndStatusAndStartIsAfter(Long ownerId, Long itemId, BookingStatus status,
                                                                        LocalDateTime now);

    List<Booking> findByItem_IdAndEndIsBefore(Long itemId, LocalDateTime now);

    List<Booking>findByItem_IdAndStartIsAfter(Long itemId, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndItem_IdAndStatusAndEndIsBefore(Long ownerId, Long itemId, BookingStatus status,
                                                                       LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndItem_IdAndStatusAndStartIsBefore(Long ownerId, Long itemId, BookingStatus status,
                                                                         LocalDateTime now);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start,
                                                               LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);
}

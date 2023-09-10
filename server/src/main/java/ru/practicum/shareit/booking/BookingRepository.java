package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT booking FROM Booking AS booking WHERE booking.item IN " +
            "(SELECT item FROM Item AS item WHERE item.owner.id = ?1)")
    Page<Booking> findByOwner_Id(Long ownerId, Pageable pageable);

    List<Booking> findByBooker_IdAndItem_IdAndStatusAndStartIsBefore(Long bookerId, Long itemId,
                                                                     BookingStatus status, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndItem_IdAndStatusAndStartIsAfter(Long ownerId, Long itemId, BookingStatus status,
                                                                        LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndItem_IdAndStatusAndStartIsBefore(Long ownerId, Long itemId, BookingStatus status,
                                                                         LocalDateTime now);

    Page<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start,
                                                               LocalDateTime end, Pageable pageable);

    Page<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByBooker_Id(Long bookerId, Pageable pageable);
}

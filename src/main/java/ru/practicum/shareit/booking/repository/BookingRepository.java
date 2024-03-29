package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "ORDER by b.start DESC")
    Page<Booking> findAllByBookerIdOrderByStartDateDesc(long bookerId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.end < CURRENT_TIMESTAMP " +
            "ORDER by b.start DESC")
    Page<Booking> findAllByBookerIdAndEndDateBefore(long bookerId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.start > CURRENT_TIMESTAMP " +
            "ORDER by b.start DESC")
    Page<Booking> findAllByBookerIdAndStartDateAfter(long bookerId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER by b.start DESC")
    Page<Booking> findAllByBookerIdAndDateBeforeAndDateAfter(long bookerId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.status = :status " +
            "ORDER by b.start DESC")
    Page<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(long bookerId, BookingStatus status, Pageable pageable);


    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = :ownerId " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerIdOrderByStartDateDesc(long ownerId, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = :ownerId " +
            "and b.end < CURRENT_TIMESTAMP " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerIdAndEndDateBefore(long ownerId, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = :ownerId " +
            "and b.start > CURRENT_TIMESTAMP " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerIdAndStartDateAfter(long ownerId, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerIdAndDateBeforeAndDateAfter(long ownerId, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = :ownerId " +
            "and b.status = :status " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerIdAndStatusOrderByStartDateDesc(long ownerId, BookingStatus status, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "and b.start < CURRENT_TIMESTAMP " +
            "ORDER by b.start DESC")
    List<Booking> findFirstByItemIdAndEndDateBefore(long itemId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "and b.start > CURRENT_TIMESTAMP " +
            "and b.status = 'APPROVED' " +
            "ORDER by b.start asc")
    List<Booking> findFirstByItemIdAndStartDateAfter(long itemId);

    List<Booking> findAllByItemIdAndBookerId(long itemId, long bookerI);
}


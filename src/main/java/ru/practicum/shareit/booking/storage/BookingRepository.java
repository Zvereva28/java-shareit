package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "ORDER by b.start DESC")
    List<Booking> findAllByBookerIdOrderByStartDateDesc(long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end < CURRENT_TIMESTAMP " +
            "ORDER by b.start DESC")
    List<Booking> findAllByBookerIdAndEndDateBefore(long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start > CURRENT_TIMESTAMP " +
            "ORDER by b.start DESC")
    List<Booking> findAllByBookerIdAndStartDateAfter(long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER by b.start DESC")
    List<Booking> findAllByBookerIdAndDateBeforeAndDateAfter(long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = ?2 " +
            "ORDER by b.start DESC")
    List<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(long bookerId, String status);


    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDateDesc(long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = ?1 " +
            "and b.end < CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndEndDateBefore(long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = ?1 " +
            "and b.start > CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartDateAfter(long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = ?1 " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndDateBeforeAndDateAfter(long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.user.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDateDesc(long ownerId, String status);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start < CURRENT_TIMESTAMP " +
            "ORDER by b.start DESC")
    List<Booking> findFirstByItemIdAndEndDateBefore(long itemId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start > CURRENT_TIMESTAMP " +
            "and b.status = 'APPROVED' " +
            "ORDER by b.start asc")
    List<Booking> findFirstByItemIdAndStartDateAfter(long itemId);

    List<Booking> findAllByItemIdAndBookerId(long itemId, long bookerI);
}


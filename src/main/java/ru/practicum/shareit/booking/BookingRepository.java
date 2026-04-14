package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // --- Поиск для АВТОРА бронирований (Booker)
    // ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    // CURRENT
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                             LocalDateTime nowStart,
                                                                             LocalDateTime nowEnd);

    // PAST
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    // FUTURE
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    // WAITING / REJECTED
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    // --- Поиск для ВЛАДЕЛЬЦА вещей (Owner) ---
    //ALL
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    // CURRENT
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                                LocalDateTime nowStart,
                                                                                LocalDateTime nowEnd);

    // PAST
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    // FUTURE
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    // WAITING / REJECTED
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    // Последнее бронирование: завершилось до "сейчас", берем самое позднее из них
    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId,
                                                                               LocalDateTime now,
                                                                               BookingStatus status);

    // Следующее бронирование: начнется после "сейчас", берем самое раннее из них
    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId,
                                                                             LocalDateTime now,
                                                                             BookingStatus status);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId,
                                                           Long itemId,
                                                           BookingStatus status,
                                                           LocalDateTime now);

}

package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false,name = "start_date")
    LocalDateTime start;

    @Column(nullable = false,name = "end_date")
    LocalDateTime end;

    @JoinColumn(name = "item_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    Item item;

    @JoinColumn(name = "booker_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    User booker;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    BookingStatus status;
}

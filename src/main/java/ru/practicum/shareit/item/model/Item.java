package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Column(nullable = false)
    @NotBlank(message = "Описание не может быть пустым")
    String description;

    @Column(nullable = false)
    @NotNull(message = "Статус доступности должен быть указан")
    Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @NotNull(message = "Владелец должен быть определен")
    User owner;

    @Column(name = "request_id")
    Long requestId;
}

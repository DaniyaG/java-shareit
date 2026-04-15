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

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Отзыв не может быть пустым")
    @Column(name = "text", nullable = false)
    String text;

    @NotNull(message = "Вещь не найдена")
    @JoinColumn(name = "item_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    Item item;

    @NotNull(message = "Автор должен быть определен")
    @JoinColumn(name = "author_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    User author;

    @NotNull(message = "Дата создания не может быть пустой")
    @Column(nullable = false)
    LocalDateTime created;

}

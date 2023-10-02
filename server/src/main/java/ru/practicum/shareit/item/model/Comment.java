package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "items_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User author;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'")
    @Column(name = "created")
    private LocalDateTime created;
}

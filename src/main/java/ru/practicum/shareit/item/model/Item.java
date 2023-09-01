package ru.practicum.shareit.item.model;

import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Component
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

}

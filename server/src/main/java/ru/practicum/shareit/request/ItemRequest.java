package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "description")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'")
    @Column(name = "created")
    private LocalDateTime created;
}

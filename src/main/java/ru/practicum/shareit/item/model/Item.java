package ru.practicum.shareit.item.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private long id;

    private String name;

    private String description;

    private Boolean available;

}

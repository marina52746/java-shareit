package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputBookingDto {
    private Long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private Long bookerId;
}

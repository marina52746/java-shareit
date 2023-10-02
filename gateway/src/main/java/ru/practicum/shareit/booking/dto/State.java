package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.UnknownStateException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State fromStringToState(String stateStr) {
        if (stateStr.isBlank())
            return State.ALL;
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stateStr)) {
                return state;
            }
        }
        throw new UnknownStateException("Unknown state: " + stateStr, "Unknown state: " + stateStr);
    }
}


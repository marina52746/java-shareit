package ru.practicum.shareit.booking;

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
        switch (stateStr) {
            case "ALL":
                return State.ALL;
            case "CURRENT":
                return State.CURRENT;
            case "PAST":
                return State.PAST;
            case "FUTURE":
                return State.FUTURE;
            case "WAITING":
                return State.WAITING;
            case "REJECTED":
                return State.REJECTED;
            default:
                throw new UnknownStateException("Unknown state: " + stateStr, "Unknown state: " + stateStr);
        }
    }
}


package ru.practicum.shareit.booking;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State fromStringToState(String stateStr) {
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
        }
        return State.ALL;
    }
}


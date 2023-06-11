package ru.yandex.practicum.filmorate.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum SortByOption {
    LIKES("likes"),
    YEAR("year");

    private final String value;

    SortByOption(String value) {
        this.value = value;
    }

    public String getValues() {
        return value;
    }

    public static SortByOption fromValue(String value) {
        if (value != null) {
            for (SortByOption option : values()) {
                if (option.value.equalsIgnoreCase(value)) {
                    return option;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
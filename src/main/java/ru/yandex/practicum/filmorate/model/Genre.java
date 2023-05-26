package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Objects;


@Data
public class Genre {
    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private Integer id;

    private String name;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Genre other = (Genre) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
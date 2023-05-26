package ru.yandex.practicum.filmorate.model;

import lombok.*;
import java.util.Objects;


@Data

public class Mpa {
    private Integer id;

    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private String name;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Mpa other = (Mpa) obj;
        return Objects.equals(id, other.id) && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}


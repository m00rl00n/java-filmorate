package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@EqualsAndHashCode(of = "id")
public class Film {
    @JsonIgnore
    private final Set<Long> likes = new HashSet<>();
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Genre> genres;
    private Mpa mpa;
    private Set<Director> directors;

    public Film() {
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate,
                Integer duration, Set<Genre> genres, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }

    public Film(Integer id,
                String name,
                String description,
                LocalDate releaseDate,
                Integer duration,
                Set<Genre> genres,
                Mpa mpa,
                Set<Director> directors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
        this.directors = directors;
    }
}


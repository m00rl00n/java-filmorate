package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> likes = new HashSet<>();

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Set<Integer> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        if (likes != null) {
            this.likes.addAll(likes);
        }
    }

    public void addLike(Integer userId) {
        if (!likes.add(userId)) {
            throw new IncorrectParameterException("Пользователь уже лайкнул этот фильм");
        }
    }

    public void removeLike(Integer userId) {
        if (!likes.remove(userId)) {
            throw new NotFoundException("Лайк данного пользователя не найден");
        }
    }

}

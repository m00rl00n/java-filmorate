package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private int idFilm = 0;
    private final Map<Integer, Film> filmsMap = new HashMap<>();

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        ++idFilm;
        film.setId(idFilm);
        filmsMap.put(idFilm, film);
        log.info("Добавлен новый фильм");
        return filmsMap.get(film.getId());
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!filmsMap.containsKey(film.getId())) {
            throw new ValidationException("Фильма нет в базе, невозможно обновить");
        }
        filmsMap.put(film.getId(), film);
        log.info("Фильм обновлен");
        return filmsMap.get(film.getId());
    }

    @GetMapping(value = "/films")
    public List<Film> getAllFilm() {
        return List.copyOf(filmsMap.values());
    }

    public void validateFilm(Film film) {
        final int MAX_DESCRIPTION_LENGTH = 200;

        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Длина описания не должна превышать " + MAX_DESCRIPTION_LENGTH + " символов");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Укажите правильную дату фильма");
        }
    }

}


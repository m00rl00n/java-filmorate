package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private int idFilm = 0;
    private final ConcurrentHashMap<Integer, Film> filmsMap = new ConcurrentHashMap<>();

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        ++idFilm;
        film.setId(idFilm);
        filmsMap.put(idFilm, film);
        log.info("Добавлен новый фильм");
        return filmsMap.get(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        if (!filmsMap.containsKey(film.getId())) {
            throw new ValidationException("Фильма нет в базе, невозможно обновить");
        }
        validateFilm(film);
        filmsMap.put(film.getId(), film);
        log.info("Фильм обновлен");
        return filmsMap.get(film.getId());
    }

    @Override
    public Film getFilm(Integer id) {
        return filmsMap.get(id);
    }

    @Override
    public List<Film> getAllFilm() {
        return List.copyOf(filmsMap.values());
    }

    @Override
    public Film deleteFilm(Film film) {
        filmsMap.remove(film.getId());
        return film;
    }


    public void validateFilm(Film film) {
        final int MAX_DESCRIPTION_LENGTH = 200;

        if (film == null) {
            throw new ValidationException("Фильм не может быть null");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Длина описания не должна превышать " + MAX_DESCRIPTION_LENGTH + " символов");
        }
        if (film.getDescription().isBlank() || film.getDescription() == null) {
            throw new ValidationException("Длина описания не должна быть пустой");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Укажите правильную дату фильма");
        }
    }
}

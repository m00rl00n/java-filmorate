package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service


public class FilmService {
    private final FilmStorage filmStorage;


    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getAllFilm() {
        return filmStorage.getAllFilm();
    }

    public Film addLike(Film film, int userId) {
        Film filmNew = filmStorage.getFilm(film.getId());
        if (filmNew == null) {
            throw new NotFoundException("Фильм не найден");
        }
        filmNew.addLike(userId);
        return filmStorage.updateFilm(filmNew);
    }

    public Film removeLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        film.removeLike(userId);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getMostPopular(int max) {
        if (max == 0) {
            max = 10;
        }
        List<Film> films = new ArrayList<>(filmStorage.getMapFilms().values());
        films.sort(Comparator.comparingInt(film -> film.getLikes().size() * (-1)));
        return films.subList(0, Math.min(films.size(), max));
    }


}


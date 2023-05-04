package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
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
        filmNew.addLike(userId);
        return filmStorage.updateFilm(filmNew);
    }

    public Film removeLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        film.removeLike(userId);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getMostPopular(int max) {
        if (max == 0) {
            max = 10;
        }
        List<Film> films = new ArrayList<>(filmStorage.getMapFilms().values());
        return filmStorage.sortByLikes(films, max);
    }


}


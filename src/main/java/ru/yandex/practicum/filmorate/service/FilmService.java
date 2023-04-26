package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor

public class FilmService {
    private final FilmStorage filmStorage;


    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
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
        Film filmLike = filmStorage.getFilm(film.getId());
        filmLike.addLike(userId);
        return filmLike;
    }

    public Film removeLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        film.removeLike(userId);
        return film;
    }

    public List<Film> getMostLikedFilms() {
        List<Film> allFilms = filmStorage.getAllFilm();
        Collections.sort(allFilms, (f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));
        return allFilms.subList(0, Math.min(allFilms.size(), 10));
    }


}


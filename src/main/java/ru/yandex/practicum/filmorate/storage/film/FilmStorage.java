package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Integer id);

    List<Film> getAllFilm();

    Film deleteFilm(Film film);

    ConcurrentHashMap<Integer, Film> getMapFilms();

    public List<Film> sortByLikes(List<Film> films, int max);

}

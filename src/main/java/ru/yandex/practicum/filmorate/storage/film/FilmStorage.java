package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Integer id);

    List<Film> getAllFilm();

    void deleteFilm(Film film);

    List<Film> sortByLikes(int max);

    List<Film> findByDirectorId(Integer id, String sortBy);

    List<Film> getCommonFilms(Integer idUser, Integer idFriend);

}

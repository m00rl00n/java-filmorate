package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Integer id);

    List<Film> getAllFilm();

    void deleteFilm(Integer id);

    List<Film> findByDirectorId(Integer id, String sortBy);

    List<Film> getFilmsByTitleParam(String title);

    List<Film> getFilmByDirectorParam(String director);

    List<Film> getFilmByBothParams(String param);

    List<Film> sortByLikes(int count, Integer genreId, Integer year);

    List<Film> getCommonFilms(Integer idUser, Integer idFriend);
}

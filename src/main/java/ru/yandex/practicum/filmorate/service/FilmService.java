package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;

import java.util.List;



@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    @Autowired
    private  final DbFilmStorage dbFilmStorage;


    public Film addFilm(Film film) {
        return dbFilmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return dbFilmStorage.updateFilm(film);
    }

    public Film getFilm(Integer id) {
        return dbFilmStorage.getFilm(id);
    }

    public List<Film> getAllFilm() {
        return dbFilmStorage.getAllFilm();
    }

    public void addLike(Integer filmId, Integer userId) {
        log.info("Добавление лайка фильму с айди " + filmId + " пользователем с айди " + userId);
        dbFilmStorage.likeFilm(filmId, userId);
    }

    public void removeLike(int id, int userId) {
        log.info("Удаление лайка у фильма с айди " + id + " пользователем с айди " + userId);
        dbFilmStorage.deleteLike(id, userId);
    }

    public void removeFilm(Film film) {
        log.info("Удаление фильма с айди " + film.getId());
       dbFilmStorage.deleteFilm(film);
    }

    public List<Integer> getLikes(Integer count) {
        return dbFilmStorage.getLikes(count);
    }

    public List<Film> getMostPopular(int max) {
        return dbFilmStorage.sortByLikes(max);
    }


}


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
    private static final String PARAM_DIRECTOR = "director";
    private static final String PARAM_TITLE = "title";
    @Autowired
    private final DbFilmStorage dbFilmStorage;


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

    public void removeFilm(Integer id) {
        log.info("Удаление фильма с айди " + id);
        dbFilmStorage.deleteFilm(id);
    }

    public List<Integer> getLikes(Integer count) {
        return dbFilmStorage.getLikes(count);
    }


    public List<Film> getByDirectorId(Integer directorId, String sortBy) {
        return dbFilmStorage.findByDirectorId(directorId, sortBy);
    }


    public List<Film> searchWithParams(String text, List<String> params) {
        boolean hasDirector = params.contains(PARAM_DIRECTOR);
        boolean hasTitle = params.contains(PARAM_TITLE);
        if (hasDirector && !hasTitle) {
            return dbFilmStorage.getFilmByDirectorParam(text);
        }
        if (hasTitle && !hasDirector) {
            return dbFilmStorage.getFilmsByTitleParam(text);
        }
        return dbFilmStorage.getFilmByBothParams(text);
    }

    public List<Film> getTopLikedFilms(Integer count, Integer genreId, Integer year) {
        return dbFilmStorage.sortByLikes(count, genreId, year);
    }


    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        return dbFilmStorage.getCommonFilms(userId, friendId);
    }

}




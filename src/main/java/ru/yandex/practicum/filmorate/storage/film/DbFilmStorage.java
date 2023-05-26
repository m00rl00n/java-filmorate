package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
public class DbFilmStorage implements FilmStorage {

    JdbcTemplate jdbcTemplate;
    FilmMapper filmMapper;
    DbUserStorage dbUserStorage;

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate, FilmMapper filmMapper, DbUserStorage dbUserStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmMapper;
        this.dbUserStorage = dbUserStorage;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        String sql = "insert into films(name, description, release_date, duration, mpa_id) "
                + "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement prepareStatement = connection.prepareStatement(sql, new String[]{"id"});
            prepareStatement.setString(1, film.getName());
            prepareStatement.setString(2, film.getDescription());
            prepareStatement.setString(3, film.getReleaseDate().toString());
            prepareStatement.setInt(4, film.getDuration());
            prepareStatement.setLong(5, film.getMpa().getId());
            return prepareStatement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null) {
            setGenres(film);
        }
        log.info("Фильм добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        String sql = "update films set  name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?"
                + "where id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            setGenres(film);
        }
        return getFilm(film.getId());
    }

    @Override
    public List<Film> getAllFilm() {
        String sql = "select * from films";
        log.info("Получение всех фильмов.......");
        return jdbcTemplate.query(sql, filmMapper);
    }


    @Override
    public Film getFilm(Integer id) {
        try {
            String sql = "select * from films where id = ?";
            log.info("Фильм получен");
            return jdbcTemplate.queryForObject(sql, filmMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с айди " + id + " не найден");
        }
    }

    public void likeFilm(Integer filmId, Integer userId) {
        dbUserStorage.getUser(userId);
        getFilm(filmId);
        String sql = "insert into likes (id_films, id_user)" + "values(?, ?)";

        jdbcTemplate.update(sql, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        dbUserStorage.getUser(userId);
        getFilm(filmId);
        String sql = "delete from likes where id_films = ? and id_user = ?";

        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Integer> getLikes(Integer filmId) {
        getFilm(filmId);
        String sql = "select id_user from likes where id_films = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("id_user"), filmId);
    }

    public List<Film> sortByLikes(int count) {
        String sql = "SELECT f.* FROM films f " +
                "LEFT JOIN likes l ON f.id = l.id_films " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(l.id_user) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, filmMapper, count);
    }

    public void setGenres(Film film) {
        Set<Genre> genreSet = new HashSet<>(film.getGenres());

        jdbcTemplate.update("delete from film_genre where id_films = ?", film.getId());

        genreSet = genreSet.stream()
                .collect(Collectors.toSet());

        List<Object[]> list = new ArrayList<>();

        for (Genre genre : genreSet) {
            Object[] values = new Object[]{film.getId(), genre.getId().intValue()};
            list.add(values);
        }

        for (Object[] values : list) {
            jdbcTemplate.update("insert into film_genre (id_films, id_genre) values (?, ?)", values);
        }

        film.setGenres(genreSet);
    }

    @Override
    public void deleteFilm(Film film) {
        String sql = "DELETE FROM films WHERE ID = ? ";
        if (jdbcTemplate.update(sql, film.getId()) == 0) {
            throw new NotFoundException("Фильм не найден!");
        }
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
        if (film.getDescription() == null || film.getDescription().isBlank()) {
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


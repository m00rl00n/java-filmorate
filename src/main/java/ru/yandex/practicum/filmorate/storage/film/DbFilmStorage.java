package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.model.Director;
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
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private final FilmMapper filmMapper;
    @Autowired
    private final DbUserStorage dbUserStorage;

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
        if (film.getDirectors() != null) {
            setDirectors(film);
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
        setDirectors(film);
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

    public List<Film> sortByLikes(int count, Integer genreId, Integer year) {
        String sql = "SELECT f.* FROM films f " +
                "LEFT JOIN likes l ON f.id = l.id_films " +
                "LEFT JOIN film_genre fg ON f.id = fg.id_films " +
                "WHERE 1 = 1 ";

        List<Object> parameters = new ArrayList<>();

        if (genreId != null) {
            sql += "AND fg.id_genre = ? ";
            parameters.add(genreId);
        }

        if (year != null) {
            sql += "AND YEAR(f.release_date) = ? ";
            parameters.add(year);
        }

        sql += "GROUP BY f.id " +
                "ORDER BY COUNT(l.id_user) DESC " +
                "LIMIT ?";

        parameters.add(count);

        return jdbcTemplate.query(sql, filmMapper, parameters.toArray());
    }

    @Override
    public List<Film> findByDirectorId(Integer id, String sortBy) {
        String sql;
        if (sortBy.equals("year")) {
            sql = "select f.*, EXTRACT(YEAR FROM CAST(f.release_date AS date)) as release_year " +
                    "from films f " +
                    "left join film_director fd on f.id = fd.id_film " +
                    "where fd.id_director = ? " +
                    "order by release_year asc";
        } else {
            sql = "select f.* from films f " +
                    "left join film_director fd on f.id = fd.id_film " +
                    "left join likes l on f.id = l.id_films " +
                    "where fd.id_director = ? " +
                    "group by f.id " +
                    "order by count(l.id_user) desc";
        }
        return jdbcTemplate.query(sql, filmMapper, id);
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

    public void setDirectors(Film film) {
        Set<Director> directorSet = film.getDirectors();
        jdbcTemplate.update("delete from film_director where id_film = ?", film.getId());
        if (directorSet != null) {
            for (Director director : directorSet) {
                jdbcTemplate.update("insert into film_director (id_film, id_director) values (?, ?)",
                        film.getId(),
                        director.getId());
            }
        }
    }

    @Override
    public void deleteFilm(Integer id) {
        getFilm(id);
        String deleteLikesQuery = "DELETE FROM likes WHERE id_films = ?";
        jdbcTemplate.update(deleteLikesQuery, id);

        String deleteFilmGenreQuery = "DELETE FROM film_genre WHERE id_films = ?";
        jdbcTemplate.update(deleteFilmGenreQuery, id);

        String deleteFilmDirectorQuery = "DELETE FROM film_director WHERE id_film = ?";
        jdbcTemplate.update(deleteFilmDirectorQuery, id);

        String deleteFilmQuery = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(deleteFilmQuery, id);
    }

    @Override
    public List<Film> getCommonFilms(Integer idUser, Integer idFriend) {
        String sql = "SELECT f.* FROM films f " +
                "INNER JOIN likes ul ON f.id = ul.id_films AND ul.id_user = ? " +
                "INNER JOIN likes fl ON f.id = fl.id_films AND fl.id_user = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(*) DESC";

        return jdbcTemplate.query(sql, filmMapper, idUser, idFriend);
    }


    public List<Film> getRecommendations(Integer userId, Integer similarUserId) {
        List<Film> recommendations = new ArrayList<>();
        String sql = "SELECT * FROM films WHERE id IN" +
                "(SELECT id_films FROM likes WHERE id_user = ? " +
                "AND id_films NOT IN (SELECT id_films FROM likes WHERE id_user = ?))";
        if (similarUserId != null) {
            recommendations = jdbcTemplate.query(sql, filmMapper, similarUserId, userId);
        }
        return recommendations;
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


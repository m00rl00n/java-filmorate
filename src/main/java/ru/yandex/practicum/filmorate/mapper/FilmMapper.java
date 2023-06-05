package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.DbDirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.DbMpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class FilmMapper implements RowMapper<Film> {

    @Autowired
    private final DbMpaStorage dbMpaStorage;
    @Autowired
    private final DbGenreStorage dbGenreStorage;

    @Autowired
    private final DbDirectorStorage dbDirectorStorage;


    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Set<Genre> genres = dbGenreStorage.getFilmGenresByFilmId(id);
        Mpa mpa = dbMpaStorage.getMpaById(rs.getInt("mpa_id"));
        Set<Director> directors = dbDirectorStorage.getByFilmId(id);

        return new Film(id, name, description, releaseDate, duration, genres, mpa, directors);
    }
}









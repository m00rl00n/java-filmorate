package ru.yandex.practicum.filmorate.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.DbMpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;


@Component

public class FilmMapper implements RowMapper<Film> {

    @Autowired
    DbMpaStorage dbMpaStorage;
    @Autowired
    DbGenreStorage dbGenreStorage;

    @Autowired
    public FilmMapper(DbMpaStorage dbMpaStorage, DbGenreStorage dbGenreStorage) {
        this.dbMpaStorage = dbMpaStorage;
        this.dbGenreStorage = dbGenreStorage;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Set<Genre> genres = dbGenreStorage.getFilmGenresByFilmId(id);
        Mpa mpa = dbMpaStorage.getMpaById(rs.getInt("mpa_id"));

        return new Film(id, name, description, releaseDate, duration, genres, mpa);
    }
}









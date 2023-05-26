package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Primary
public class DbMpaStorage implements MpaStorage {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        List<Mpa> all = new ArrayList<>();
        String sql = "SELECT * FROM Mpa";
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(sql);
        while (mpaRow.next()) {
            all.add(new Mpa(mpaRow.getInt("id"), mpaRow.getString("name")));
        }
        return all;
    }

    @Override
    public Mpa getMpaById(Integer id) {
        String sql = "SELECT * FROM Mpa WHERE id = ?";
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(sql, id);
        if (mpaRow.first()) {
            return new Mpa(mpaRow.getInt("id"), mpaRow.getString("name"));
        } else {
            throw new NotFoundException("MPA не найден.");
        }
    }
}
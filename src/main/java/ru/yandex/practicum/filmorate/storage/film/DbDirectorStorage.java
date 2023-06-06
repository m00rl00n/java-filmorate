package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class DbDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbDirectorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sql = "select * from directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> this.makeDirector(rs));
    }

    @Override
    public Director findById(Integer id) {
        String sql = "select * from directors where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> this.makeDirector(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Директор с айди " + id + " не найден");
        }
    }

    @Override
    public Director save(Director director) {
        validateDirector(director);
        String sql = "insert into directors (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Запись добавлена: directors with id = " + director.getId());
        return director;
    }

    @Override
    public Director update(Director director) {
        validateDirector(director);
        String sql = "update directors set name = ? where id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        log.info("Запись обновлена: directors with id = " + director.getId());
        return this.findById(director.getId());
    }

    @Override
    public void delete(Integer id) {
        String sql = "delete from directors where id = ?";
        int count = jdbcTemplate.update(sql, id);
        if (count > 0) {
            log.info("Запись удалена: directors with id = " + id);
        }
    }

    @Override
    public Set<Director> getByFilmId(Integer id) {
        String sql = "select d.* from directors d " +
                "left join film_director fd on d.id = fd.id_director " +
                "where fd.id_film = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> this.makeDirector(rs), id));
    }

    private Director makeDirector(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        return new Director(id, name);
    }

    private void validateDirector(Director director) {
        if (!StringUtils.hasText(director.getName())) {
            throw new ValidationException("Имя директора не может быть пустым");
        }
    }
}

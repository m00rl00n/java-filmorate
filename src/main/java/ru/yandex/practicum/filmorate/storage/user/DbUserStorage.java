package ru.yandex.practicum.filmorate.storage.user;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;


@Component
@Primary
@Slf4j
public class DbUserStorage implements UserStorage {

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;


    @Override
    public User addUser(User user) {
        validateUser(user);
        if (user == null) {
            throw new ValidationException("Нужно добавить пользователя");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement prepareStatement = connection.prepareStatement(sql, new String[]{"id"});
            prepareStatement.setString(1, user.getEmail());
            prepareStatement.setString(2, user.getLogin());
            prepareStatement.setString(3, user.getName());
            prepareStatement.setString(4, user.getBirthday().toString());
            return prepareStatement;
        }, keyHolder);

        if (keyHolder.getKey() instanceof Number) {
            Number generatedId = keyHolder.getKey();
            user.setId(generatedId.intValue());
        } else {
            throw new IllegalStateException("Не удалось получить сгенерированный ключ");
        }

        log.info("Регистрация пользователя завершена");
        return user;
    }

    public User updateUser(User user) {
        getUser(user.getId());
        validateUser(user);
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday().toString(), user.getId());
        log.info("Пользователь обновлен");
        return user;
    }

    @Override
    public List<User> getUsers() {

        String sql = "select * from users";
        log.info("Получение всех пользователей......");
        return jdbcTemplate.query(sql, userMapper);

    }

    @Override
    public User getUser(Integer userId) {
        try {
            String sql = "select * from users where  id = ?";
            return jdbcTemplate.queryForObject(sql, userMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с айди " + userId + " не найден");
        }
    }

    @Override
    public void deleteUser(Integer userId) {
        getUser(userId);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql,userId);
        log.info("Пользователь с айди " + userId + " удален");
    }


    public void addFriend(Integer userId, Integer friendId) {
        getUser(userId);
        getUser(friendId);
        String sql = "INSERT INTO friends (id_user, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql,userId, friendId);
        log.info("Друг добавлен");
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        getUser(userId);
        getUser(friendId);
        String sql = "DELETE FROM friends WHERE id_user = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Друг удален");
    }

    @Override
    public List<User> getFriends(Integer userId) {
        getUser(userId);
        String sql = "SELECT u.id, u.email, u.name, u.login, u.birthday " +
                "FROM friends AS f " +
                "JOIN users AS u ON f.friend_id = u.id " +
                "WHERE f.id_user = ? " +
                "ORDER BY u.id";
        return jdbcTemplate.query(sql, userMapper, userId);
    }

    @Override
    public List<User> checkCommonFriends(Integer userId, Integer otherId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM friends AS f " +
                "JOIN users AS u ON f.friend_id = u.id " +
                "WHERE f.id_user = ? " +
                "AND f.friend_id IN (SELECT friend_id FROM friends WHERE id_user = ?)";
        return jdbcTemplate.query(sql, userMapper, userId, otherId);
    }
    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("Email должен быть заполнен и содержать символ @");
        } else if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        } else if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}

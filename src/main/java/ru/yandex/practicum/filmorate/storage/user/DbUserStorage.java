package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.PreparedStatement;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private final UserMapper userMapper;
    @Autowired
    private final EventStorage eventStorage;

    @Override
    public User addUser(User user) {
        validateUser(user);
        if (user == null) {
            throw new ValidationException("Нужно добавить пользователя");
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
    public User getUser(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userMapper, id);
        if (users.isEmpty()) {
            throw new NotFoundException("Пользователь с айди " + id + " не найден");
        }
        return users.get(0);
    }

    @Override
    public void deleteUser(Integer id) {
        getUser(id);
        String deleteFriendsQuery = "DELETE FROM friends WHERE id_user = ? OR friend_id = ?";
        jdbcTemplate.update(deleteFriendsQuery, id, id);
        String deleteUserQuery = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(deleteUserQuery, id);

        log.info("Пользователь удален");
    }

    public void addFriend(Integer userId, Integer friendId) {
        getUser(userId);
        getUser(friendId);
        String sql = "INSERT INTO friends (id_user, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Друг добавлен");
        eventStorage.add(new UserEvent(
                null,
                Instant.now().toEpochMilli(),
                userId,
                EventType.FRIEND,
                Operation.ADD,
                friendId));
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        getUser(userId);
        getUser(friendId);
        String sql = "DELETE FROM friends WHERE id_user = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Друг удален");

        eventStorage.add(new UserEvent(
                null,
                Instant.now().toEpochMilli(),
                userId,
                EventType.FRIEND,
                Operation.REMOVE,
                friendId));
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

    public Integer getIdUserWithMostOverlappingLikes(int userId) {
        Integer similarUserId = null;
        SqlRowSet findIdUser = jdbcTemplate.queryForRowSet(
                "SELECT id_user, COUNT(id_user) as films_intersect_n FROM likes " +
                        "WHERE id_films IN (SELECT id_films FROM likes WHERE id_user = ?) AND id_user != ? " +
                        "GROUP BY id_user ORDER BY films_intersect_n desc LIMIT 1", userId, userId);
        if (findIdUser.next()) {
            similarUserId = findIdUser.getInt("id_user");
        }
        return similarUserId;
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

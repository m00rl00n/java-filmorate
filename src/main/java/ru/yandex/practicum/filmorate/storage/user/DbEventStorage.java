package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
@Slf4j
public class DbEventStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventMapper eventMapper;
    private final Map<String, Integer> operationsMap;
    private final Map<String, Integer> eventTypesMap;

    public DbEventStorage(JdbcTemplate jdbcTemplate,
                          EventMapper eventMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventMapper = eventMapper;
        operationsMap = this.getAllOperations();
        eventTypesMap = this.getAllEventTypes();
    }

    @Override
    public List<UserEvent> findByUserId(Integer id) {
        String findUserSql = "SELECT 1 FROM users WHERE id = ?";
        SqlRowSet user = jdbcTemplate.queryForRowSet(findUserSql, id);
        if (!user.next()) {
            throw new NotFoundException("User not found");
        }
        String sql = "SELECT ue.*, o.name AS operation, et.name AS event_type " +
                "FROM user_events ue " +
                "LEFT JOIN operations o ON ue.operation_id = o.id " +
                "LEFT JOIN event_types et ON ue.event_type_id = et.id " +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sql, eventMapper, id);
    }

    @Override
    public UserEvent add(UserEvent event) {
        String sql = "INSERT INTO user_events (event_date, user_id, entity_id, event_type_id, operation_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement prepareStatement = connection.prepareStatement(sql, new String[]{"id"});
            prepareStatement.setTimestamp(1, new Timestamp(event.getTimestamp()));
            prepareStatement.setInt(2, event.getUserId());
            prepareStatement.setInt(3, event.getEntityId());
            prepareStatement.setInt(4, eventTypesMap.get(event.getEventType().name()));
            prepareStatement.setInt(5, operationsMap.get(event.getOperation().name()));
            return prepareStatement;
        }, keyHolder);

        if (keyHolder.getKey() instanceof Number) {
            Number generatedId = keyHolder.getKey();
            event.setEventId(generatedId.intValue());
        } else {
            throw new IllegalStateException("Не удалось получить сгенерированный ключ");
        }
        log.info("Событие зарегистрировано");
        return event;
    }

    private Map<String, Integer> getAllOperations() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT * FROM operations";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            map.put(rowSet.getString("name"), rowSet.getInt("id"));
        }
        return map;
    }

    private Map<String, Integer> getAllEventTypes() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT * FROM event_types";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            map.put(rowSet.getString("name"), rowSet.getInt("id"));
        }
        return map;
    }
}

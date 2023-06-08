package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class ReviewMapper implements RowMapper<Review> {

    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer reviewId = rs.getInt("id");
        String content = rs.getString("content");
        boolean isPositive = rs.getBoolean("is_positive");
        int useful = rs.getInt("EVALUATION_SUM");
        int userId = rs.getInt("id_user");
        int filmId = rs.getInt("id_film");
        return new Review(reviewId, content, isPositive,  useful, userId, filmId);
    }
}

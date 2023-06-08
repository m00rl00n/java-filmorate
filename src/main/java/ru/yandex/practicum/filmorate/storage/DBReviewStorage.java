package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Primary
@RequiredArgsConstructor
public class DBReviewStorage implements ReviewStorage {

    @Autowired
    private final ReviewMapper reviewMapper;

    private final Logger log = LoggerFactory.getLogger(DBReviewStorage.class);

    @Autowired
    private final JdbcTemplate jdbcTemplate;



    @Override
    public Review add(Review review) {
        log.info("Создание отзыва");
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("reviews")
                    .usingGeneratedKeyColumns("id");
            int id = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();
            review.setReviewId(id);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Не найден пользователь или фильм");
        }
        return review;
    }

   /* @Override
    public Review add(Review review) {
        log.info("Создание отзыва");
        try {
            String sql = "insert into reviews (content,is_positive,id_user,id_film) values (?,?,?,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement(sql, new String[]{"id"});
                statement.setString(1, review.getContent());
                statement.setBoolean(2, review.getIsPositive());
                statement.setInt(3, review.getUserId());
                statement.setInt(4, review.getFilmId());
                return statement;
            }, keyHolder);
            review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            return review;
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Не найден пользователь или фильм");
        }
    }*/

    @Override
    public Review edit(Review review) {
        log.info("Обновление отзыва");
        String sqlQuery = "UPDATE reviews SET " +
                "content = ?, is_positive = ? " +
                "WHERE id = ?";
        if (jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.isPositive(),
                review.getReviewId()) > 0) {
            return getById(review.getReviewId());
        } else {
            throw new NotFoundException("Отзыв для обновления отсутствует");
        }
    }

    @Override
    public void delete(int id) {
        log.info("Удаление отзыва");
        String sqlQuery = "DELETE FROM reviews WHERE id = ?";
        if (jdbcTemplate.update(sqlQuery,
                id) > 0) {
        } else {
            throw new NotFoundException("Отзыв для удаления отсутствует");
        }
    }

    @Override
    public Review getById(int id) {
        log.info("Получение отзыва");
        String sqlQuery = "SELECT r.*, EVALUATION_SUM FROM REVIEWS r \n" +
                "LEFT JOIN (\n" +
                "\tSELECT ID_REVIEW, SUM(re.EVALUATION) AS EVALUATION_SUM FROM reviews_evaluation re \n" +
                "\tGROUP BY ID_REVIEW\n" +
                "\t) re ON r.ID = re.ID_REVIEW\n" +
                "WHERE r.ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, reviewMapper, id);
    }

    @Override
    public List<Review> getReviews() {
        log.info("Получение всех отзывов");
        String sqlQuery = "SELECT r.*, EVALUATION_SUM FROM REVIEWS r \n" +
                "LEFT JOIN (\n" +
                "\tSELECT ID_REVIEW, SUM(re.EVALUATION) AS EVALUATION_SUM FROM reviews_evaluation re \n" +
                "\tGROUP BY ID_REVIEW\n" +
                "\t) re ON r.ID = re.ID_REVIEW\n";
        return jdbcTemplate.query(sqlQuery, reviewMapper);
    }

    @Override
    public List<Review> getByIdFilm(int idFilm, int count) {
        log.info("Получение отзывов");
        String sqlQuery = "SELECT r.*, EVALUATION_SUM FROM REVIEWS r \n" +
                "LEFT JOIN (\n" +
                "\tSELECT ID_REVIEW, SUM(re.EVALUATION) AS EVALUATION_SUM FROM reviews_evaluation re \n" +
                "\tGROUP BY ID_REVIEW\n" +
                "\t) re ON r.ID = re.ID_REVIEW ";
        List<Object> parameters = new ArrayList<>();
        if (idFilm != 0) {
            sqlQuery += "WHERE r.ID_FILM = ? ";
            parameters.add(idFilm);
        }
        sqlQuery += "LIMIT ?";
        parameters.add(count);
        return jdbcTemplate.query(sqlQuery, reviewMapper, parameters);
    }

    @Override
    public void setLike(int idReview, int idUser, int rate) {
        log.info("Установка лайк/дизлайк");
        String sqlQuery = "INSERT INTO REVIEWS_EVALUATION (ID_REVIEW, ID_USER, EVALUATION) VALUES (?,?,?)";
        if (jdbcTemplate.update(sqlQuery,
                idReview,
                idUser,
                rate) == 0)
        {
           throw new NotFoundException("Отзыв для обновления отсутствует");
        }
    }

    @Override
    public void deleteLike(int idReview, int idUser) {
        log.info("Установка лайк/дизлайк");
        String sqlQuery = "DELETE  FROM REVIEWS_EVALUATION WHERE ID_REVIEW = ? AND ID_USER = ?";
        if (jdbcTemplate.update(sqlQuery,
                idReview,
                idUser) == 0)
        {
            throw new NotFoundException("Отзыв для обновления отсутствует");
        }
    }

    private void updateRateRewiew(int idReview) {
        log.info("Обновление рейтинга");
    }
}

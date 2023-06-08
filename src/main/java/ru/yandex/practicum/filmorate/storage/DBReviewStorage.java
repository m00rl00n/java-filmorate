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
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;


import java.util.ArrayList;
import java.util.List;


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
        userIdIsExist(review.getUserId());
        filmIdIsExist(review.getFilmId());
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
        if (jdbcTemplate.update(sqlQuery, id) == 0) {
            throw new NotFoundException("Отзыв для удаления отсутствует");
        }
    }

    @Override
    public Review getById(int id) {
        try {
            log.info("Получение отзыва");
            String sqlQuery = "SELECT r.*, EVALUATION_SUM FROM REVIEWS r \n" +
                    "LEFT JOIN (\n" +
                    "\tSELECT ID_REVIEW, SUM(re.EVALUATION) AS EVALUATION_SUM FROM reviews_evaluation re \n" +
                    "\tGROUP BY ID_REVIEW\n" +
                    "\t) re ON r.ID = re.ID_REVIEW\n" +
                    "WHERE r.ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, reviewMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв с айди " + id + " не найден");
        }
    }

    @Override
    public List<Review> getReviews() {
        log.info("Получение всех отзывов");
        String sqlQuery = "SELECT r.*, COALESCE(re.EVALUATION_SUM,0) AS EVALUATION_SUM FROM REVIEWS r \n" +
                "LEFT JOIN (\n" +
                "\tSELECT ID_REVIEW, SUM(re.EVALUATION) AS EVALUATION_SUM FROM reviews_evaluation re \n" +
                "\tGROUP BY ID_REVIEW\n" +
                "\t) re ON r.ID = re.ID_REVIEW\n ORDER BY EVALUATION_SUM DESC";
        return jdbcTemplate.query(sqlQuery, reviewMapper);
    }

    @Override
    public List<Review> getByIdFilm(int idFilm, int count) {
        log.info("Получение отзывов");
        String sqlQuery = "SELECT r.*, COALESCE(re.EVALUATION_SUM,0) AS EVALUATION_SUM FROM REVIEWS r \n" +
                "LEFT JOIN (\n" +
                "\tSELECT ID_REVIEW, SUM(re.EVALUATION) AS EVALUATION_SUM FROM reviews_evaluation re \n" +
                "\tGROUP BY ID_REVIEW\n" +
                "\t) re ON r.ID = re.ID_REVIEW ";
        List<Object> parameters = new ArrayList<>();
        if (idFilm != 0) {
            sqlQuery += "WHERE r.ID_FILM = ? ";
            parameters.add(idFilm);
        }
        sqlQuery += "ORDER BY EVALUATION_SUM DESC ";
        sqlQuery += "LIMIT ?";
        parameters.add(count);
        return jdbcTemplate.query(sqlQuery, reviewMapper, parameters.toArray());
    }

    @Override
    public void setLike(int idReview, int idUser, int rate) {
        log.info("Установка лайк/дизлайк");
        String sqlQuery = "INSERT INTO REVIEWS_EVALUATION (ID_REVIEW, ID_USER, EVALUATION) VALUES (?,?,?)";
        if (jdbcTemplate.update(sqlQuery,
                idReview,
                idUser,
                rate) == 0) {
           throw new NotFoundException("Отзыв для обновления отсутствует");
        }
    }

    @Override
    public void deleteLike(int idReview, int idUser) {
        log.info("Установка лайк/дизлайк");
        String sqlQuery = "DELETE  FROM REVIEWS_EVALUATION WHERE ID_REVIEW = ? AND ID_USER = ?";
        if (jdbcTemplate.update(sqlQuery,
                idReview,
                idUser) == 0) {
            throw new NotFoundException("Отзыв для обновления отсутствует");
        }
    }

    private void userIdIsExist(int id) {
        boolean b = Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT id FROM users WHERE id = ?)", Boolean.class, id));
        if (!b) {
            log.error("Передан некорректный id пользователя" + id);
            throw new NotFoundException("Некорректный id " + id);
        }
    }

    private void filmIdIsExist(int id) {
        boolean b = Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT id FROM films WHERE id = ?)", Boolean.class, id));
        if (!b) {
            log.error("Передан некорректный id фильма" + id);
            throw new NotFoundException("Некорректный id " + id);
        }
    }

}

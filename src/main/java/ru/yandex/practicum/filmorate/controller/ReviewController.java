package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    @PostMapping("/reviews")
    public ResponseEntity<Review> createReview(@Valid @RequestBody Review review) {
        log.info("Создание отзыва");
        return new ResponseEntity<Review>(reviewService.add(review), HttpStatus.OK);
    }

    @PutMapping("/reviews")
    public ResponseEntity<Review> updateReview(@Valid @RequestBody Review review) {
        log.info("Обновление отзыва");
        return new ResponseEntity<Review>(reviewService.edit(review), HttpStatus.OK);
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable @Positive(message = "id отзыва не положительное число") int id) {
        log.info("Удаление отзыва");
        reviewService.delete(id);
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable @Positive(message = "id отзыва не положительное число") int id) {
        log.info("Получение отзыва");
        return new ResponseEntity<Review>(reviewService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getReviewsByFilmID(@RequestParam(required = false, defaultValue = "0") int filmId, @RequestParam(required = false, defaultValue = "0") int count) {
        log.info("Получение отзывов");
        return new ResponseEntity<List<Review>>(reviewService.getByIdFilm(filmId, count), HttpStatus.OK);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void setLike(@PathVariable @Positive(message = "id отзыва не положительное число") int id, @PathVariable @Positive(message = "userId отзыва не положительное число") int userId) {
        log.info("Установка лайка");
        reviewService.setLike(id, userId, true);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void setDislike(@PathVariable @Positive(message = "id отзыва не положительное число") int id, @PathVariable @Positive(message = "userId отзыва не положительное число") int userId) {
        log.info("Установка дизлайка");
        reviewService.setLike(id, userId, false);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void deleteLike(@PathVariable @Positive(message = "id отзыва не положительное число") int id, @PathVariable @Positive(message = "userId отзыва не положительное число") int userId) {
        log.info("Удаление дизлайка");
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable @Positive(message = "id отзыва не положительное число") int id, @PathVariable @Positive(message = "userId отзыва не положительное число") int userId) {
        log.info("Установка дизлайка");
        reviewService.deleteLike(id, userId);
    }

}

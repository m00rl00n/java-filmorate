package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.DBReviewStorage;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    DBReviewStorage dbReviewStorage;

    public Review add(Review review) {
        return dbReviewStorage.add(review);
    }

    public Review edit(Review review) {
        return dbReviewStorage.edit(review);
    }

    public void delete(int id) {
        dbReviewStorage.delete(id);
    }

    public Review getById(int id) {
        return dbReviewStorage.getById(id);
    }

    public List<Review> getReviews() {
        return dbReviewStorage.getReviews();
    }

    public List<Review> getByIdFilm(int idFilm, int count)
    {
        if (idFilm == 0 && count == 0) {
            return dbReviewStorage.getReviews();
        } else {
            if (count == 0) {
                count = 10;
            }
            return dbReviewStorage.getByIdFilm(idFilm, count);
        }
    }

    public void setLike(int idReview, int idUser, boolean isLike) {
        dbReviewStorage.setLike(idReview, idUser, isLike ? 1 : -1);
    }

    public void deleteLike(int idReview, int idUser) {
        dbReviewStorage.deleteLike(idReview,idUser);
    }

}

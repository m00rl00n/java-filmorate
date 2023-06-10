package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review add(Review review);

    Review edit(Review review);

    void delete(int id);

    Review getById(int id);

    List<Review> getByIdFilm(int idFilm, int count);

    List<Review> getReviews();

    void setLike(int idReview, int idUser, int rate);

    void deleteLike(int idReview, int idUser);

}

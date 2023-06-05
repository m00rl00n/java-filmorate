package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {
    List<Director> findAll();

    Director findById(Integer id);

    Director save(Director director);

    Director update(Director director);

    void delete(Integer id);

    Set<Director> getByFilmId(Integer id);
}

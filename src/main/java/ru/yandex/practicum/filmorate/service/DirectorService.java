package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {
    @Autowired
    private DirectorStorage directorStorage;

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(Integer id) {
        return directorStorage.findById(id);
    }

    public Director add(Director director) {
        return directorStorage.save(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public void delete(Integer id) {
        directorStorage.delete(id);
    }
}

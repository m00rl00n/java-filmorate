package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.DbMpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    @Autowired
    private final DbMpaStorage dbMpaStorage;

    public List<Mpa> getAllMpa() {
        return dbMpaStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer id) {
        return dbMpaStorage.getMpaById(id);
    }
}


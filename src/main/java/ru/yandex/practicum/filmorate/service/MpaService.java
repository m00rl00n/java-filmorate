package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.DbMpaStorage;

import java.util.List;

@Service

public class MpaService {
    DbMpaStorage dbMpaStorage;

    @Autowired
    public MpaService(DbMpaStorage dbMpaStorage) {
        this.dbMpaStorage = dbMpaStorage;
    }

    public List<Mpa> getAllMpa() {
        return dbMpaStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer id) {
        return dbMpaStorage.getMpaById(id);
    }
}


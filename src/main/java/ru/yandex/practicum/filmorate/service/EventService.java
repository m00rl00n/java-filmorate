package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.storage.user.EventStorage;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventStorage eventStorage;

    public List<UserEvent> findByUserId(Integer id) {
        return eventStorage.findByUserId(id);
    }
}

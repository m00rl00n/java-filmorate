package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.List;

public interface EventStorage {
    List<UserEvent> findByUserId(Integer id);

    UserEvent add(UserEvent event);
}

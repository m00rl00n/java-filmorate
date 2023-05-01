package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUser(Integer userId);

    User deleteUser(User user);

    List<Integer> getFriends(Integer id);

    ConcurrentHashMap<Integer, User> getMap();
}
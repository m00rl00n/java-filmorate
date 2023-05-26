package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.util.List;


@Service
public class UserService {

    private final DbUserStorage dbUserStorage;

    @Autowired
    public UserService(DbUserStorage dbUserStorage) {
        this.dbUserStorage = dbUserStorage;
    }

    public User addUser(User user) {
        return dbUserStorage.addUser(user);
    }

    public User updateUser(User user) {
        return dbUserStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return dbUserStorage.getUsers();
    }

    public User getUser(Integer userId) {
        return dbUserStorage.getUser(userId);
    }

    public void deleteUser(Integer userId) {
        dbUserStorage.deleteUser(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        dbUserStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        dbUserStorage.deleteFriend(userId, friendId);
    }


    public List<User> getFriends(int id) {
        return dbUserStorage.getFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return dbUserStorage.checkCommonFriends(id, otherId);
    }
}




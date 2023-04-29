package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(Integer userId) {
        return userStorage.getUser(userId);
    }

    public User addFriend(Integer userId, Integer friendId) {
        if (!(userStorage.getMap().containsKey(userId) && userStorage.getMap().containsKey(friendId))) {
            throw new NotFoundException("Пользователь не найден");
        }
        User user1 = userStorage.getUser(userId);
        User user2 = userStorage.getUser(friendId);
        if (user1 == null || user2 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        user1.addFriend(friendId);
        user2.addFriend(userId);
        return user1;
    }

    public void removeFriend(Integer user, Integer friend) {
        User inMemoryUser = userStorage.getUser(user);
        if (inMemoryUser.getFriends().contains(friend)) {
            inMemoryUser.removeFriend(friend);
        }
    }

    public List<Integer> getCommonFriends(Integer user1Id, Integer user2Id) {
        User user1 = userStorage.getUser(user1Id);
        User user2 = userStorage.getUser(user2Id);
        Set<Integer> commonFriendsSet = new HashSet<>(user1.getFriends());
        commonFriendsSet.retainAll(user2.getFriends());
        return new ArrayList<>(commonFriendsSet);
    }

    public List<Integer> getFriends(Integer id) {
        return userStorage.getFriends(id);
    }
}
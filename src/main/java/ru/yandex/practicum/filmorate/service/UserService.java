package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
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
        User user1 = userStorage.getUser(userId);
        User user2 = userStorage.getUser(friendId);
        user1.addFriend(friendId);
        user2.addFriend(userId);
        return user1;
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user1 = userStorage.getUser(userId);
        User user2 = userStorage.getUser(friendId);
        userStorage.checkCommonFriends(userId, friendId);
        user1.removeFriend(friendId);
        user2.removeFriend(userId);
    }


    public List<User> getFriends(int id) {
        User user = userStorage.getUser(id);
        List<User> friends = new ArrayList<>();
        Set<Integer> friendId = user.getFriends();
        for (Integer idFriend : friendId) {
            User friend = userStorage.getUser(idFriend);
            if (friend != null) {
                friends.add(friend);
            }
        }

        return friends;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        List<User> userFriends = getFriends(id);
        List<User> otherUserFriends = getFriends(otherId);
        List<User> commonFriends = new ArrayList<>();
        for (User userFriend : userFriends) {
            if (otherUserFriends.contains(userFriend)) {
                commonFriends.add(userFriend);
            }
        }

        return commonFriends;
    }
}




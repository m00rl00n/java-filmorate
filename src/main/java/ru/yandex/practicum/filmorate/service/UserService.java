package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public User addUser(User user){
        return userStorage.addUser(user);
    }
    public User updateUser(User user){
        return userStorage.updateUser(user);
    }
    public List<User> getUsers(){
        return userStorage.getUsers();
    }
    public User getUser(Integer userId) {
    return userStorage.getUser(userId);
    }
    public void addFriend(Integer id, Integer friend) {
        User user1 = userStorage.getUser(id);
        User user2= userStorage.getUser(friend);
        user1.addFriend(friend);
        user2.addFriend(id);
    }
    public void deleteFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }
    public List<Integer> getCommonFriends(Integer user1Id, Integer user2Id) {
        User user1 = userStorage.getUser(user1Id);
        User user2 = userStorage.getUser(user2Id);
        Set<Integer> commonFriendsSet = new HashSet<>(user1.getFriends());
        commonFriendsSet.retainAll(user2.getFriends());
        return new ArrayList<>(commonFriendsSet);
    }

    public List<Integer> getFriends(Integer id){
        return userStorage.getFriends(id);
    }
}
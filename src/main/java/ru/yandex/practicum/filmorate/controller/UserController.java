package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService service) {
        this.userService = service;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        return userService.getUser(id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("userId") Integer userId) {
        userService.deleteUser(userId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable("userId") Integer userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("userId") Integer userId,
                                       @PathVariable("otherId") Integer otherId) {
        return userService.getCommonFriends(userId, otherId);
    }
}
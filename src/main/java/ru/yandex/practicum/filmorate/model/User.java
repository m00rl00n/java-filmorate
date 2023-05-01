package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data

public class User {

    private int id;

    private String email;

    private String login;

    private String name;

    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public User(int id, String email, String login, String name, LocalDate birthday, Set<Integer> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        if (friends != null) {
            this.friends.addAll(friends);
        }
    }

    public void addFriend(Integer friend) {
        if (friend == null) {
            throw new IncorrectParameterException("ID не может быть нулевым");
        }
        if (friends.contains(friend)) {
            throw new IncorrectParameterException("Данный пользователь уже является другом");
        }
        friends.add(friend);
    }

    public void removeFriend(Integer friend) {
        if (friend == null) {
            throw new IncorrectParameterException("ID не может быть нулевым");
        }
        if (!friends.contains(friend)) {
            throw new NotFoundException("Пользователь с id " + friend + " не является другом");
        }
        friends.remove(friend);
    }
}



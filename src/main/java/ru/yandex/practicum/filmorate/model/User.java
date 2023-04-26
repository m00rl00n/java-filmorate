package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    @NonNull
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends;
    public void addFriend(Integer friend) {
        if (friend == null) {
            throw new IncorrectParameterException("ID не может быть нулевым");
        }
        if (friends.contains(friend)) {
            throw new  IncorrectParameterException("Данный пользователь уже является другом");
        }
        friends.add(friend);
    }

    public void removeFriend(Integer friend) {
        if (friend == null) {
            throw new  IncorrectParameterException("ID не может быть нулевым");
        }
        if (!friends.contains(friend)) {
            throw new NotFoundException("Пользователь с id " + friend + " не является другом");
        }
        friends.remove(friend);
    }
}



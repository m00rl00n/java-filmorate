package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
public class UserController {
    private final Map<Integer, User> usersMap = new HashMap<>();
    private int idUser = 0;

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        validateUser(user);
        ++idUser;
        user.setId(idUser);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        usersMap.put(user.getId(), user);
        log.info("Пользователь зарегистрирован");
        return usersMap.get(user.getId());
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {

        if (!usersMap.containsKey(user.getId())) {
            throw new ValidationException("Пользователь не зарегистрирован");
        }
        validateUser(user);
        usersMap.put(user.getId(), user);
        log.info("Данные пользователя обновлены");
        return usersMap.get(user.getId());
    }

    @GetMapping(value = "/users")
    public List<User> getAllUser() {
        return List.copyOf(usersMap.values());
    }

    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email должен быть заполнен и содержать символ @");
        } else if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        } else if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }


}
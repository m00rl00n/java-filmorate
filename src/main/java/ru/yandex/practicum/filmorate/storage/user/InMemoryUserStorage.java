package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final ConcurrentHashMap<Integer, User> usersMap = new ConcurrentHashMap<>();
    private int idUser = 0;

    @Override
    public User addUser(User user) {
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

    @Override
    public User updateUser(User user) {
        if (!usersMap.containsKey(user.getId())) {
            throw new ValidationException("Пользователь не зарегистрирован");
        }
        validateUser(user);
        usersMap.put(user.getId(), user);
        log.info("Данные пользователя обновлены");
        return usersMap.get(user.getId());
    }

    @Override
    public List<User> getUsers() {
            return List.copyOf(usersMap.values());

    }

    @Override
    public User getUser(Integer userId) {
        if (!usersMap.containsKey(userId)) {
            throw new ValidationException("Пользователь не найден.");
        }
        return usersMap.get(userId);
    }


    @Override
    public List<Integer> getFriends(Integer id) {
        return  new ArrayList<>(usersMap.get(id).getFriends());
    }

    @Override
    public User deleteUser(User user) {
        usersMap.remove(user.getId());
        return user;
    }

    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("Email должен быть заполнен и содержать символ @");
        } else if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        } else if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}

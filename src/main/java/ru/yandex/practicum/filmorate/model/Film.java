package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    @NonNull
    private int id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> likes;

    public void addLike(Integer userId) {
        if (likes.contains(userId)) {
            throw new IncorrectParameterException("Пользователь уже лайкнул этот фильм");
        }
        likes.add(userId);
    }

    public void removeLike(Integer userId) {
        if (!likes.contains(userId)) {
            throw new NotFoundException("Лайк данного пользователя не найден");
        }
        likes.remove(userId);
    }

}

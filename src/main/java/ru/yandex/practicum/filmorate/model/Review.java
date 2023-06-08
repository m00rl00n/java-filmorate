package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Getter
@Data
public class Review {

    private int reviewId;

    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 200, message = "Превышена максимальная длина описания(100)")
    private String content;

    @JsonProperty(value = "isPositive")
    @NotNull
    private Boolean isPositive;

    private int useful;

    @NotNull(message = "Не указан пользователь")
    private Integer userId;

    @NotNull(message = "Не указан фильм")
    private Integer filmId;

    public Review() {
    }

    public Review(int reviewId, String content, boolean isPositive, int useful, int userId, int filmId) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.useful = useful;
        this.userId = userId;
        this.filmId = filmId;
    }

    @JsonProperty(value = "isPositive")
    public boolean isPositive() {
        return isPositive;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("id_user", userId);
        values.put("id_film", filmId);
        return values;
    }

}

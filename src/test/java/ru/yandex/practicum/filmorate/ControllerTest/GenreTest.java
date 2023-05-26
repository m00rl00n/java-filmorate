package ru.yandex.practicum.filmorate.ControllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class GenreTest {

    private final GenreController genreController;

    @Autowired
    public GenreTest(GenreController genreController) {
        this.genreController = genreController;
    }

    @Test
    void getAllGenresTest() {
        Genre genre1 = genreController.getGenreById(1);
        Genre genre2 = genreController.getGenreById(2);
        Genre genre3 = genreController.getGenreById(3);
        Genre genre4 = genreController.getGenreById(4);
        Genre genre5 = genreController.getGenreById(5);
        Genre genre6 = genreController.getGenreById(6);

        List<Genre> genreList = genreController.getAllGenres();
        assertThat(genreList).isNotNull();
        assertThat(genreList.isEmpty()).isFalse();
        assertThat(genreList.contains(genre1)).isTrue();
        assertThat(genreList.contains(genre2)).isTrue();
        assertThat(genreList.contains(genre3)).isTrue();
        assertThat(genreList.contains(genre4)).isTrue();
        assertThat(genreList.contains(genre5)).isTrue();
        assertThat(genreList.contains(genre6)).isTrue();
    }
}

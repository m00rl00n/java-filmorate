package ru.yandex.practicum.filmorate.ControllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
public class FilmTest {
    @Autowired
    public FilmTest(FilmController filmController, UserController userController) {
        this.filmController = filmController;
        this.userController = userController;
    }


    private FilmController filmController;
    private final UserController userController;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        Mpa mpa = new Mpa(1, "G");
        testFilm = new Film(1, "название", "описание", LocalDate.of(2023, 1, 1),
                150, new HashSet<>(), mpa);
    }

    @Test
    void addFilmTest() {
        Film addedFilm = filmController.addFilm(testFilm);
        assertThat(addedFilm).isNotNull();
        assertThat(addedFilm.getName()).isEqualTo(testFilm.getName());
        assertThat(addedFilm.getDescription()).isEqualTo(testFilm.getDescription());
        assertThat(addedFilm.getReleaseDate()).isEqualTo(testFilm.getReleaseDate());
        assertThat(addedFilm.getDuration()).isEqualTo(testFilm.getDuration());
        assertThat(addedFilm.getMpa()).isEqualTo(testFilm.getMpa());
    }

    @Test
    void updateFilmTest() {
        Film addedFilm = filmController.addFilm(testFilm);

        addedFilm.setName("новое название");
        addedFilm.setDescription("новое описание");
        addedFilm.setReleaseDate(LocalDate.of(2024, 2, 14));
        addedFilm.setDuration(120);

        Film updatedFilm = filmController.updateFilm(addedFilm);
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo(addedFilm.getName());
        assertThat(updatedFilm.getDescription()).isEqualTo(addedFilm.getDescription());
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(addedFilm.getReleaseDate());
        assertThat(updatedFilm.getDuration()).isEqualTo(addedFilm.getDuration());
        assertThat(updatedFilm.getMpa()).isEqualTo(addedFilm.getMpa());
    }

    @Test
    void getAllFilmsTest() {
        filmController.addFilm(testFilm);
        filmController.addFilm(new Film(2, "название2", "описание2", LocalDate.of(2023, 2, 2),
                120, new HashSet<>(), new Mpa(2, "PG-13")));

        List<Film> films = filmController.getAllFilms();
        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(9);
    }

    @Test
    void getFilmTest() {
        Film addedFilm = filmController.addFilm(testFilm);
        Film retrievedFilm = filmController.getFilm(addedFilm.getId());
        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm.getName()).isEqualTo(addedFilm.getName());
        assertThat(retrievedFilm.getDescription()).isEqualTo(addedFilm.getDescription());
        assertThat(retrievedFilm.getReleaseDate()).isEqualTo(addedFilm.getReleaseDate());
        assertThat(retrievedFilm.getDuration()).isEqualTo(addedFilm.getDuration());
        assertThat(retrievedFilm.getMpa()).isEqualTo(addedFilm.getMpa());
    }

    @Test
    void likeFilmTest() {
        User user = new User(1, "test@1.com", "логин", "имя", LocalDate.of(2013, 9, 1));
        userController.addUser(user);
        Film addedFilm = filmController.addFilm(testFilm);
        filmController.likeFilm(addedFilm.getId(), 1);

        List<Integer> likes = filmController.getLikes(addedFilm.getId());
        assertThat(likes).isNotNull();
        assertThat(likes.size()).isEqualTo(1);
        assertThat(likes.get(0)).isEqualTo(1);
    }

    @Test
    void deleteLikeTest() {
        Film addedFilm = filmController.addFilm(testFilm);
        filmController.likeFilm(addedFilm.getId(), 1);
        filmController.deleteLike(addedFilm.getId(), 1);

        List<Integer> likes = filmController.getLikes(addedFilm.getId());
        assertThat(likes).isNotNull();
        assertThat(likes.size()).isZero();
    }

    @Test
    void getPopularFilmsTest() {
        filmController.addFilm(testFilm);
        filmController.addFilm(new Film(2, "название2", "описание2", LocalDate.of(2023, 2, 2),
                120, new HashSet<>(), new Mpa(2, "PG-13")));

        List<Film> popularFilms = filmController.getPopularFilms(1);
        assertThat(popularFilms).isNotNull();
        assertThat(popularFilms.size()).isEqualTo(1);
    }

}





package ru.yandex.practicum.filmorate.ControllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmTest {

    @Autowired
    private FilmController filmController;

    @Test
    void addFilmTest() {
        Film newFilm = new Film(1, "название", "описание", LocalDate.of(2023, 1, 1), 150);
        Film addedFilm = filmController.addFilm(newFilm);
        assertEquals(newFilm.getName(), addedFilm.getName());
        assertEquals(newFilm.getDescription(), addedFilm.getDescription());
        assertEquals(newFilm.getReleaseDate(), addedFilm.getReleaseDate());
        assertEquals(newFilm.getDuration(), addedFilm.getDuration());
    }

    @Test
    void updateFilmTest() {
        Film newFilm = new Film(1, "Название", "Описание", LocalDate.of(2023, 1, 1), 150);
        Film addedFilm = filmController.addFilm(newFilm);
        addedFilm.setName("Новое название");
        addedFilm.setDescription("Новое описание");
        addedFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        Film updatedFilm = filmController.updateFilm(addedFilm);
        assertEquals(addedFilm.getId(), updatedFilm.getId());
        assertEquals(addedFilm.getName(), updatedFilm.getName());
        assertEquals(addedFilm.getDescription(), updatedFilm.getDescription());
        assertEquals(addedFilm.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(addedFilm.getDuration(), updatedFilm.getDuration());
    }

    @Test
    void getAllFilmTest() {
        Film newFilm1 = new Film(1, "Название1", "Описание 1", LocalDate.of(2023, 1, 1), 150);
        Film newFilm2 = new Film(2, "Название2", "Описание 2", LocalDate.of(2021, 2, 2), 110);
        Film addedFilm1 = filmController.addFilm(newFilm1);
        Film addedFilm2 = filmController.addFilm(newFilm2);
        List<Film> filmsList = filmController.getAllFilm();
        assertEquals(3, filmsList.size());
        assertTrue(filmsList.contains(addedFilm1));
        assertTrue(filmsList.contains(addedFilm2));
    }


    @Test
    public void testValidateFilmEmptyName() {

        Film newFilm = new Film(1, "", "Описание 1", LocalDate.of(2023, 1, 1), 150);
        assertThrows(ValidationException.class, () -> {
            filmController.validateFilm(newFilm);
        });
    }

    @Test
    public void testValidateFilmLongDescription() {
        String longDescription = "a".repeat(201);
        Film newFilm = new Film(1, "Название1", longDescription, LocalDate.of(2023, 1, 1), 150);

        assertThrows(ValidationException.class, () -> {
            filmController.validateFilm(newFilm);
        });
    }

    @Test
    public void testValidateFilmNegativeDuration() {
        Film newFilm = new Film(1, "Название1", "Описание 1", LocalDate.of(2023, 1, 1), -1);

        assertThrows(ValidationException.class, () -> {
            filmController.validateFilm(newFilm);
        });
    }

    @Test
    public void testValidateFilmInvalidReleaseDate() {
        Film newFilm = new Film(1, "Название1", "Описание 1", LocalDate.of(1800, 1, 1), 150);

        assertThrows(ValidationException.class, () -> {
            filmController.validateFilm(newFilm);
        });
    }

}
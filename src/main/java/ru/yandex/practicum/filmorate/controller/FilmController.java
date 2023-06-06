package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        filmService.addFilm(film);
        return filmService.getFilm(film.getId());
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilm();
    }

    @DeleteMapping
    public void deleteFilm(@RequestBody Film film) {
        filmService.removeFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(name = "count",
                    defaultValue = "10", required = false) Integer count,
            @RequestParam(name = "genreId", required = false) Integer genreId,
            @RequestParam(name = "year", required = false) Integer year
    ) {
        return filmService.getTopLikedFilms(count, genreId, year);
    }

    @GetMapping("/{id}/like")
    public List<Integer> getLikes(Integer count) {
        return filmService.getLikes(count);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirectorId(@PathVariable("directorId") Integer id,
                                      @RequestParam(required = false) String sortBy) {
        if (sortBy != null) {
            if (!sortBy.equals("likes") && !sortBy.equals("year")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else {
            sortBy = "year";
        }

        List<Film> list = filmService.getByDirectorId(id, sortBy);
        if (list.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return list;
    }

}




package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryFilmService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class FilmController {

    private final InMemoryFilmService filmService;

    private final static String DEFAULT_RATING_COUNT = "10";

    @GetMapping(value = "/films/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film getFilm(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @PostMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping(value = "/films/{id}/like/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film addLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        return filmService.addLike(userId, filmId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film removeLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        return filmService.removeLike(userId, filmId);
    }

    @GetMapping(value = "/films/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Film> getMostPopularFilms(@RequestParam(defaultValue = DEFAULT_RATING_COUNT) Integer count) {
        return filmService.getMostPopularFilms(count);
    }
}

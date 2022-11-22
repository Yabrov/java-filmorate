package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    private Integer nextId = 1;

    @PostMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film addFilm(@Valid @RequestBody Film film) {
        film.setId(nextId++);
        log.info("{} created.", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new FilmNotFoundException(film);
        }
        log.info("Фильм с id={} успешно обновлен.", film.getId());
        films.replace(film.getId(), film);
        return film;
    }

    @GetMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Film> getAllFilms() {
        return films.values();
    }
}

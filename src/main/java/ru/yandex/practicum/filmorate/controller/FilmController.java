package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFound;
import ru.yandex.practicum.filmorate.exception.WrongFilmReleaseDate;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private final static LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();

    private Integer nextId = 1;

    @PostMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film addFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Недопустимая дата релиза фильма: '{}'", film.getReleaseDateString());
            throw new WrongFilmReleaseDate(film);
        }
        film.setId(nextId++);
        log.info("Film with id={} created.", film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Невозможно обновить фильм с id=null.");
            throw new FilmNotFound(film);
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Недопустимая дата релиза фильма: '{}'", film.getReleaseDateString());
            throw new WrongFilmReleaseDate(film);
        }
        if (films.containsKey(film.getId())) {
            log.info("Фильм с id={} успешно обновлен.", film.getId());
            films.replace(film.getId(), film);
            return film;
        } else {
            log.error("Фильм с id={} не найден.", film.getId());
            throw new FilmNotFound(film);
        }
    }

    @GetMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Film> getAllFilms() {
        return films.values();
    }
}
